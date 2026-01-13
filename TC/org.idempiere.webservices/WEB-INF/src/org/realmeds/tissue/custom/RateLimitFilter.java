package org.realmeds.tissue.custom;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.compiere.model.MSysConfig;

@RateLimited
public class RateLimitFilter implements ContainerRequestFilter {

	@Context
	private HttpServletRequest httpRequest;

	private static final Map<String, RequestStats> REQUEST_LOG = new ConcurrentHashMap<>();

	private static final int DEFAULT_MAX_ATTEMPTS = 7;
	private static final int DEFAULT_TIME_WINDOW_SECONDS = 600;
	private static final int DEFAULT_MAX_MULTIPLIER = 8;
    
    private static int MAX_ATTEMPTS;
    private static long TIME_WINDOW_MILLIS;
    private static int MAX_MULTIPLIER;

    static {
        loadConfiguration();
    }
    
    private static void loadConfiguration() {
        MAX_ATTEMPTS = getSystemConfigValue("RATELIMIT_MAX_ATTEMPTS", DEFAULT_MAX_ATTEMPTS);
        int timeWindowSeconds = getSystemConfigValue("RATELIMIT_TIME_WINDOW_SECONDS", DEFAULT_TIME_WINDOW_SECONDS);
        TIME_WINDOW_MILLIS = (long) timeWindowSeconds * 1000;
        MAX_MULTIPLIER = getSystemConfigValue("RATELIMIT_MAX_MULTIPLIER", DEFAULT_MAX_MULTIPLIER);
        
    }

    private static int getSystemConfigValue(String key, int defaultValue) {
          return MSysConfig.getIntValue(key, defaultValue, 0);
    }
    

	private static class RequestStats {
		long lastAttemptTime;
		int attemptCount;
        int failureMultiplier;
		long lockoutStartTime;
		int lockoutViolationCount;

		public RequestStats(long time) {
			this.lastAttemptTime = time;
			this.attemptCount = 1;
            this.failureMultiplier = 1;
			this.lockoutStartTime = 0;
			this.lockoutViolationCount = 0;
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String clientIp = getClientIpAddress(httpRequest);

		long currentTime = System.currentTimeMillis();

		RequestStats currentStats = REQUEST_LOG.compute(clientIp, (ip, stats) -> {
			if (stats == null) {
				return new RequestStats(currentTime);
			}

			if (stats.lockoutStartTime > 0) {
				long lockoutDuration = TIME_WINDOW_MILLIS * stats.failureMultiplier;
				long lockoutEndTime = stats.lockoutStartTime + lockoutDuration;

				if (currentTime >= lockoutEndTime) {
					stats.attemptCount = 1;
					stats.lockoutStartTime = 0;
					stats.lockoutViolationCount = 0;
					stats.lastAttemptTime = currentTime;
				} else {
					stats.lockoutViolationCount++;
					if (stats.lockoutViolationCount > MAX_ATTEMPTS) {
						stats.failureMultiplier = Math.min(stats.failureMultiplier + 1, MAX_MULTIPLIER);
						stats.lockoutStartTime = currentTime;
						stats.lockoutViolationCount = 0;
					}
				}
				return stats;
			}

			if (currentTime - stats.lastAttemptTime > TIME_WINDOW_MILLIS) {
				stats.attemptCount = 1;
				stats.failureMultiplier = 1;
			} else {
				stats.attemptCount++;
			}
			stats.lastAttemptTime = currentTime;
			return stats;
		});

		if (currentStats.lockoutStartTime > 0) {
			long lockoutDuration = TIME_WINDOW_MILLIS * currentStats.failureMultiplier;
			long lockoutEndTime = currentStats.lockoutStartTime + lockoutDuration;
			long waitTime = lockoutEndTime - currentTime;
			long waitTimeInSeconds = (waitTime / 1000) + 1;

			Response response = Response.status(429).header("Retry-After", waitTimeInSeconds)
					.entity("Rate limit exceeded. Try again in " + waitTimeInSeconds + " seconds.").build();
			requestContext.abortWith(response);
			return;
		}

		if (currentStats.attemptCount > MAX_ATTEMPTS) {
			currentStats.lockoutStartTime = currentTime;
			currentStats.lockoutViolationCount = 0;
			long waitTimeInSeconds = (TIME_WINDOW_MILLIS * currentStats.failureMultiplier) / 1000;

			Response response = Response.status(429).header("Retry-After", waitTimeInSeconds)
					.entity("Rate limit exceeded. Try again in " + waitTimeInSeconds + " seconds.").build();
			requestContext.abortWith(response);
			return;
		}
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String xForwardedForHeader = request.getHeader("X-Forwarded-For");

		if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()
				&& !"unknown".equalsIgnoreCase(xForwardedForHeader)) {

			return xForwardedForHeader.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}