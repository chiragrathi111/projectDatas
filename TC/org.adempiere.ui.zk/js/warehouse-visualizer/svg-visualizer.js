var draw;
var menuDraw;
var WHLayout = {
	cfg: {
		viewBox: {
			width: '100%',
			height: '800'
		},
		defaultBinWidth: 40,
		defaultBinGutterWidth: 10,
		defaultRackSpacing: 40,
		zones: [{
			name: 'Default',
			color: '#F8F7FC',
			occupancyColors: [],
			coords: {x: 0, y: 0},
			width: 600,
			height: 600,
			aisles: 7,
			aisleCfg: {
				labelFn: 'alphabetical',
				levelsPerSide: 5,
				binsPerLevel: 20 
			}
		}, {
			name: 'Dispatch',
			color: '#f6edfa',
			occupancyColors: [],
			coords: {x: 640, y: 0},
			width: 400,
			height: 400,
			aisles: 5,
			aisleCfg: {
				labelFn: 'alphabetical',
				levelsPerSide: 5,
				binsPerLevel: 20 
			}
		}, {
			name: 'Cold Storage',
			color: '#edeffa',
			occupancyColors: [],
			coords: {x: 640, y: 410},
			width: 800,
			height: 200,
			aisles: 3,
			aisleCfg: {
				labelFn: 'alphabetical',
				levelsPerSide: 5,
				binsPerLevel: 40 
			}
		}]
	},
	viewBox: {
		coords: {x: 0, y: 0},
		width: 0,
		height: 0
	}
};


var occupancy = [0, 10, 20, 40, 60, 100];
var COLORS = ['#BCF4A4', '#aba446', '#f5f55f', '#f0c560', '#f79a36', '#FC1907'];
var origBinColor;

var occupancyDetail = {
	'Default': ['A-L-1-1', 'A-L-2-4', 'A-L-3-4']
}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}

var generateColorRange = function (start, end, steps) {
    var startRGB = hexToRgb(start),
        endRGB = hexToRgb(end);

	var colors = [];

		for (i = 0; i < steps; i++) {
			if (startRGB.r < endRGB.r) {
                startRGB.r += 1;
            }else if(startRGB.r > endRGB.r){
            startRGB.r -= 1;
            }
            if (startRGB.g < endRGB.g) {
                startRGB.g += 1;
            }else if(startRGB.g > endRGB.g){
                startRGB.g -= 1;
            }
            if (startRGB.b < endRGB.b) {
                startRGB.b += 1;
            }else if(startRGB.b > endRGB.b){
                startRGB.b -= 1;
            }
			colors.push('rgb(' + startRGB.r + ',' + startRGB.g + ',' + startRGB.b + ')');
		}

		return colors;
}

function init(){
	console.log('Creating SVG...');
  	draw = SVG().addTo('.svgDrawing').size(WHLayout.cfg.viewBox.width, WHLayout.cfg.viewBox.height);
	console.log('draw: ', draw, draw.bbox());

	WHLayout.viewBox.coords = { x: draw.node.clientLeft, y: draw.node.clientTop };
	WHLayout.viewBox.height = draw.node.clientHeight;
	WHLayout.viewBox.width = draw.node.clientWidth;

	// generateColorRange('#BCF4A4', '#FC1907', 101);
	// generateColorRange('#BCF4A4', '#FC1907', 101);

  	//draw.rect(100, 100).attr({ fill: '#bada55' });

  	addLayout();	
	addSensorData(640+800-120, 410-20);

}

function addLayout(layout) {
	
	layout = layout || WHLayout.cfg;
	
	if (draw) {
		layout.zones.forEach((z, idx) => {
			
			var rackCnt = z.aisles - 1;

			console.log('Calling addRacksToZone: ', z, rackCnt);
			// addRacksToZone(z, rackCnt);
			addRacksToZoneResponsive(z, rackCnt);
			
			// draw.rect(100, 100).move(idx*200, idx*200).attr({ fill: z.color });
		});

		addLegend(1100, 100);
	} else {
		console.log('unexpected draw value. Nothing to draw');
	}
	
}

function addRacksToZone(zone, rackCnt) {
	var binW = WHLayout.cfg.defaultBinWidth;
	var binH = binW;

	var rackW = binW*zone.aisleCfg.binsPerLevel + (zone.aisleCfg.binsPerLevel + 1)*WHLayout.cfg.defaultBinGutterWidth;
	var rackH = 2*binH + WHLayout.cfg.defaultBinGutterWidth;

	var zoneW = rackW + 2*WHLayout.cfg.defaultRackSpacing;
	var zoneH = rackCnt*rackH + zone.aisles*WHLayout.cfg.defaultRackSpacing;

	draw.rect(zoneW, zoneH).move(zone.x, zone.y),attr({ fill: zone.color }).addClass('zone-' + zone.name);

	const alpha = Array.from(Array(26)).map((e, i) => i + 65);
	const alphabet = alpha.map((x) => String.fromCharCode(x));


	for (var i = 0; i < rackCnt; i++) {
		var rackGrp = draw.group().addClass('rack-' + i);
		// zoneGrp.add(rackGrp);

		// var rackW = WHLayout.viewBox.width - 2*WHLayout.cfg.defaultRackSpacing;
		// var rackH = (WHLayout.viewBox.height - zone.aisles*WHLayout.cfg.defaultRackSpacing)/rackCnt;
		
		var xr = zone.coords.x + WHLayout.cfg.defaultRackSpacing;
		var yr = zone.coords.y + (i+1)*WHLayout.cfg.defaultRackSpacing + i*rackH;

		// var binW = (rackW - (zone.aisleCfg.binsPerLevel + 1)*WHLayout.cfg.defaultBinGutterWidth)/zone.aisleCfg.binsPerLevel;
		// var binH = rackH/2 - WHLayout.cfg.defaultBinGutterWidth;

		//show aisel label
		var text = draw.text(alphabet[i])
		text.move(xr - 20,yr - 20).font({ fill: '#f06', family: 'Inconsolata' })

		// console.log('xr, yr: ', xr, yr, rackW, rackH, binW, binH);
		
		for (var j = 0; j < zone.aisleCfg.binsPerLevel; j++) {
			//side 1
			var xb1 = xr + WHLayout.cfg.defaultBinGutterWidth;
			var yb1 = yr;

			// console.log('xb, yb: ', xb1 + j*(WHLayout.cfg.defaultBinGutterWidth + binW), yb1);

			rackGrp.rect(binW, binH).move(xb1 + j*(WHLayout.cfg.defaultBinGutterWidth + binW), yb1).attr({ fill: zone.color });

			//side 2
			var xb2 = xr + WHLayout.cfg.defaultBinGutterWidth;
			var yb2 = yb1 + WHLayout.cfg.defaultBinGutterWidth + binH;

			// console.log('xb, yb: ', xb2 + j*(WHLayout.cfg.defaultBinGutterWidth + binW), yb2);

			rackGrp.rect(binW, binH).move(xb2 + j*(WHLayout.cfg.defaultBinGutterWidth + binW), yb2).attr({ fill: '#fada55' });

		}
		
	}
}

function addRacksToZoneResponsive(zone, rackCnt) {
	// var rackW = WHLayout.viewBox.width - 2*WHLayout.cfg.defaultRackSpacing;
	// var rackH = (WHLayout.viewBox.height - zone.aisles*WHLayout.cfg.defaultRackSpacing)/rackCnt;

	var rackW = zone.width - 2*WHLayout.cfg.defaultRackSpacing;
	var rackH = (zone.height - zone.aisles*WHLayout.cfg.defaultRackSpacing)/rackCnt;

	if (rackH < WHLayout.cfg.defaultRackSpacing) {
		WHLayout.cfg.defaultRackSpacing = rackH;
		rackH = (zone.height - zone.aisles*WHLayout.cfg.defaultRackSpacing)/rackCnt;
	}

	var binW = (rackW - (zone.aisleCfg.binsPerLevel + 1)*WHLayout.cfg.defaultBinGutterWidth)/zone.aisleCfg.binsPerLevel;
	var binH = rackH/2 - WHLayout.cfg.defaultBinGutterWidth;
	if (binW < WHLayout.cfg.defaultBinGutterWidth) {
		WHLayout.cfg.defaultBinGutterWidth = binW;
		binW = (rackW - (zone.aisleCfg.binsPerLevel + 1)*WHLayout.cfg.defaultBinGutterWidth)/zone.aisleCfg.binsPerLevel;
	}

	var zoneW = rackW + 2*WHLayout.cfg.defaultRackSpacing;
	var zoneH = rackCnt*rackH + zone.aisles*WHLayout.cfg.defaultRackSpacing;

	draw.rect(zoneW, zoneH).move(zone.coords.x, zone.coords.y).attr({ fill: zone.color }).addClass('zone-' + zone.name);

	const alpha = Array.from(Array(26)).map((e, i) => i + 65);
	const alphabet = alpha.map((x) => String.fromCharCode(x));

	//show zone label
	var zoneLbl = draw.text(zone.name);
	zoneLbl.move(zone.coords.x + zoneW/2,zone.coords.y + 10).font({ fill: '#f06', family: 'Inconsolata' });


	for (var i = 0; i < rackCnt; i++) {
		var rackGrp = draw.group().addClass('rack-' + i);
		// zoneGrp.add(rackGrp);

		var xr = zone.coords.x + WHLayout.cfg.defaultRackSpacing;
		var yr = zone.coords.y + (i+1)*WHLayout.cfg.defaultRackSpacing + i*rackH;

		//show aisel label
		var text = draw.text(alphabet[i]);
		text.move(xr - 20,yr - 20).font({ fill: '#f06', family: 'Inconsolata' });

		// console.log('xr, yr: ', xr, yr, rackW, rackH, binW, binH);

		zone.occupancyColors = generateColorRange('#BCF4A4', '#FC1907', zone.aisleCfg.levelsPerSide + 1);

		// console.log('colors: ', zone.occupancyColors);

		for (var j = 0; j < zone.aisleCfg.binsPerLevel; j++) {
			//side 1
			var xb1 = xr + WHLayout.cfg.defaultBinGutterWidth;
			var yb1 = yr;

			// console.log('xb, yb: ', xb1 + j*(WHLayout.cfg.defaultBinGutterWidth + binW), yb1);

			var bin1OccupancyVal = Math.floor(Math.random() * 6);
			var binColor1 = COLORS[bin1OccupancyVal];

			var side1bin = rackGrp.rect(binW, binH).move(xb1 + j*(WHLayout.cfg.defaultBinGutterWidth + binW), yb1).attr({ id: 'bin-' + i + '' + j + '-' + bin1OccupancyVal,fill: binColor1 }).addClass('bin');
			side1bin.on('mouseenter', onBinEnter);
			side1bin.on('mouseout', onBinOut);

			//side 2
			var xb2 = xr + WHLayout.cfg.defaultBinGutterWidth;
			var yb2 = yb1 + WHLayout.cfg.defaultBinGutterWidth + binH;

			// console.log('xb, yb: ', xb2 + j*(WHLayout.cfg.defaultBinGutterWidth + binW), yb2);

			var bin2OccupancyVal = Math.floor(Math.random() * 6);
			var binColor2 = COLORS[bin2OccupancyVal];
			var side2bin = rackGrp.rect(binW, binH).move(xb2 + j*(WHLayout.cfg.defaultBinGutterWidth + binW), yb2).attr({ id: 'bin-' + i + '' + j + '-' + bin2OccupancyVal, fill: binColor2 }).addClass('bin');
			side2bin.on('mouseenter', onBinEnter);
			side2bin.on('mouseout', onBinOut);
		}

		//show last aisel label
		if (i == rackCnt - 1) {
			var text = draw.text(alphabet[rackCnt]);
			text.move(xr - 20,yr + 2* binH + WHLayout.cfg.defaultBinGutterWidth + 20).font({ fill: '#f06', family: 'Inconsolata' });
		}
		
	}
}

function addLevelsToRack() {
	
}

function addBinsToLevel() {
	
}

function addLegend(x, y) {
	var legendGrp = draw.group().move(x, y);

	draw.text('Occupancy Legend').move(x, y - 30).font({ fill: '#000000', family: 'Inconsolata' });

	draw.rect(20, 20).move(x, y).attr({fill: COLORS[0]});
	draw.text('- 0%').move(x + 30, y).font({ fill: '#000000', family: 'Inconsolata' });

	draw.rect(20, 20).move(x, y+30).attr({fill: COLORS[1]});
	draw.text('- 20%').move(x + 30, y+30).font({ fill: '#000000', family: 'Inconsolata' });

	draw.rect(20, 20).move(x, y+60).attr({fill: COLORS[2]});
	draw.text('- 40%').move(x + 30, y+60).font({ fill: '#000000', family: 'Inconsolata' });

	draw.rect(20, 20).move(x, y+90).attr({fill: COLORS[3]});
	draw.text('- 60%').move(x + 30, y+90).font({ fill: '#000000', family: 'Inconsolata' });

	draw.rect(20, 20).move(x, y+120).attr({fill: COLORS[4]});
	draw.text('- 80%').move(x + 30, y+120).font({ fill: '#000000', family: 'Inconsolata' });

	draw.rect(20, 20).move(x, y+150).attr({fill: COLORS[5]});
	draw.text('- 100%').move(x + 30, y+150).font({ fill: '#000000', family: 'Inconsolata' });
}

function addSensorData(x, y) {
	draw.image('images/temperature.png').size(20, 20).move(x, y);
	draw.text('3°C').move(x+25, y);

	draw.image('images/moisture.png').size(20, 20).move(x + 60, y);
	draw.text('40%').move(x+60+25, y);
}

function addMenuContent(occupancyVal) {
	menuDraw = SVG().addTo('.svgMenu').size(150, 200);

	occupancyVal = occupancyVal || 0;

	menuDraw.rect(200, 30).attr({fill: COLORS[occupancyVal]});;
	menuDraw.text('Occupancy - ' + (occupancyVal/5)*100 + '%').move(10, 5);

	menuDraw.text('Level 5').move(10, 40);
	menuDraw.rect(20, 20).move(60, 40).attr({ fill: 5 <= occupancyVal ? COLORS[0] :  COLORS[5]});

	menuDraw.text('Level 4').move(10, 70);
	menuDraw.rect(20, 20).move(60, 70).attr({ fill: 4 <= occupancyVal ? COLORS[0] :  COLORS[5] });

	menuDraw.text('Level 3').move(10, 100);
	menuDraw.rect(20, 20).move(60, 100).attr({ fill: 3 <= occupancyVal ? COLORS[0] :  COLORS[5] });

	menuDraw.text('Level 2').move(10, 130);
	menuDraw.rect(20, 20).move(60, 130).attr({ fill: 2 <= occupancyVal ? COLORS[0] :  COLORS[5] });

	menuDraw.text('Level 1').move(10, 160);
	menuDraw.rect(20, 20).move(60, 160).attr({ fill: 1 <= occupancyVal ? COLORS[0] :  COLORS[5] });
}

function onBinEnter(e) {
	// console.log('onBinEnter: ', this, e, this.node.id);

	origBinColor = this.css('fill');

	var occupancyVal = this.node.id.split('-')[2];

	this.css({fill: '#000000'});
	// this.css({opacity: '0.1'});

	// this.fill({color: '#000000'});

	console.log('occupancyVal: ', occupancyVal);

	addMenuContent(occupancyVal);

	var menuEls = document.getElementsByClassName('svgMenu');
	menuEls[0].style.left = (e.offsetX + 10) + 'px';
	menuEls[0].style.top = (e.offsetY + 10) + 'px';
	menuEls[0].style.visibility = 'visible';
}

function onBinOut(e) {
	// console.log('onBinOut: ', this, e);
	this.css({fill: origBinColor});
	// this.css({opacity: '1'});

	console.log('>>> origBinColor: ', origBinColor);

	var menuEls = document.getElementsByClassName('svgMenu');
	menuEls[0].style.visibility = 'hidden';

	if (menuDraw) {
		menuDraw.remove();
	}
}