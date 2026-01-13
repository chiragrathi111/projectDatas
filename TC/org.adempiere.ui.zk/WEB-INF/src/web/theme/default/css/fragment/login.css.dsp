.login-window {
	/*background-color: #E5E5E5;*/
}

.login-window .z-window-content {
	/*background-color: #E5E5E5;*/
}

.login-box-body {
	width: 660px;
	background-image: url(../images/login-box-bg.png);
	background-repeat: repeat-y;
	background-color: transparent;
	z-index: 1;
	padding: 0;
	margin: 0;
	text-align: center;
	padding-bottom: 100px;
}

.login-box-header {
	/*background-image: url(../images/login-box-header.png);
	background-color: transparent;
	z-index: 2;
	height: 54px;
	width: 660px;*/
	margin-bottom: 2%;
}

.login-right-cont{
    margin:auto;
    margin-top: 23% ;
	/* margin:auto; */
	width: 70% !important;
	height: 14vh !important;
}

.login-box-header-txt {
	font-size:24px !important;
 	font-weight: bold;
}

.login-box-role-selection-header-txt {
	font-size:24px !important;
	font-weight: bold;
}

.login-box-logo {
	padding-top: 20px;
	padding-bottom: 25px;
	background-color: #E5E5E5;
	text-align:center;
	margin-bottom:90px;
}

.login-box-footer {
	/*background-image: url(../images/login-box-footer.png);
	background-position: top center;
	background-attachment: scroll;
	background-repeat: repeat-y;*/
	z-index: 2;
	height: 6vh;
	width: 100%;
}

.login-box-footer .confirm-panel {
	width: inherit;
}

.login-box-footer-pnl {
	width: 604px;
	margin-left: 10px;
	margin-right: 10px;
	padding-top: 8% !important;
}

.role-box-footer {
	margin-top: 2%;
}

.login-label {
	color: #081735;
	text-align: left;
	padding: 0px 6px 6px 6px !important;
}

.login-label span {
	font-weight: 400;
}

.login-field {
	text-align: center;
	width: 55%;
}

.login-input-field{
	width:100%;
	padding-bottom: 2% !important;
}

.login-input-field input {
	color: #94A3B8;
	background:#FFFFFF !important;
	text-align: left;
    width: inherit;
    padding: 10px;
    padding-left: 48px;
    border-radius: 8px;
    border: 1px solid #298F60;
}


.login-input-field input:focus{
	border: 1px solid #298F60;
    background: rgba(37, 99, 235, 0.10);
}

.login-input-dropdown{
	padding-bottom: 2% !important;
	width: 100%;
	position: relative;
}

.login-input-dropdown input {
	color: #94A3B8;
	text-align:left;
	width: 100%;
    padding: 16px;
    padding-left:48px;
    border-radius: 8px;
	border: 1px solid #298F60;
	background: #FFFFFF;
}



.login-input-dropdown span{
	width: 100% !important;
}

.login-input-dropdown input:focus{
	border: 1px solid #298F60;
    background: #EAF4EF;
}


.login-input-dropdown a {
	top: 8px !important;
    right: 8px !important;
    background: none !important;
    border: none !important;
}

.login-remember-me {
	width: 100%;
	padding-bottom:2% !important;
	display: flex;
    justify-content: space-between;
}

.login-remember-me label{
	padding-left:6px;
}

.forgot-pwd{
	color:#298F60 !important;
}

.login-version-copyright-cont{
    margin: auto;
    width: 100%;
    padding: 2px;
    margin-top: 5%;
	display:flex;
	justify-content: space-between;
}

.login-version-copyright-cont span{
	color:#64748B !important;
}

.login-welcome-note-cont{
 	padding-bottom: 4% !important;
}

.login-welcome-note{
    font-weight: bold;
    font-size: 24px !important;
    text-align:center !important;
   
}

.login-welcome-note-subtitle{
	text-align: center;
	padding-bottom: 8% !important;
}

.login-welcome-note-subtitle span {
	font-size: 12px !important;
}

.login-note{
	font-weight: bold;
    font-size: 32px !important;
    text-align:center;
    color:#298F60 !important;
}

.login-btn {
	width:100%;
	background:#298F60 !important;
	color:#fff !important;
	padding: 8px;
	border-radius: 8px;
	border: 1px solid #298F60;
}
.login-btn [class^="z-icon-"]{
	color:#fff !important;
	display:none;
}

.login-button-role-cancel{
	background:#fff !important;
	color:#298F60 !important;
	padding: 14px;
	border: none !important;
}

.login-button-role-cancel:focus {
	box-shadow:none;
}

.login-fields-cont {
	width:100%;
}

.login-east-panel, .login-west-panel {
	width: 50% !important;
	background-color: #298F60;
	position: relative;
	text-align:center;
}
@media screen and (max-width: 659px) {
	.login-box-body, .login-box-header, .login-box-footer {
		background-image: none;
		width: 90%;
	}
	.login-box-footer .confirm-panel, .login-box-footer-pnl {
		width: 90% !important;
	}
	.login-box-header-txt {
		display: none;
	}
}
@media screen and (max-height: 600px) {
	.login-box-header-txt {
		display: none;
	}
	.login-box-body, .login-box-header, .login-box-footer {
		background-image: none;
	}
	.login-box-body {
		padding-bottom: 10px;
	}
	.login-box-header {
		height: 0px;
	}
}
@media screen and (max-width: 359px) {
	.login-window .z-center > .z-center-body .z-window.z-window-embedded > .z-window-content {
		padding: 0px
	}
}

/*wms overrides*/

.login-input-field{
	position: relative;		
}

.login-input-field .login-user{
	position: absolute;
    top: 8px;
    left: 16px;
    font-size: 16px;
}

 .login-input-field .login-password{
	position: absolute;
    top: 8px;
    left: 16px;
    font-size: 20px;
}

.login-input-dropdown .login-language, .role-tenent, .role-calender{
	position: absolute;
	top: 8px;
	left: 16px;
	font-size: 16px;
	z-index: 1;
}

.login-input-dropdown .role{
	position: absolute;
	top: 6px;	
	left: 16px;
	font-size: 16px;
	z-index: 1;
}

.login-input-dropdown .role-users {
	position: absolute;
	top: 8px;	
	left: 16px;
	font-size: 16px;
	z-index: 1;
}

.login-input-dropdown .role-warehouse{
	position: absolute;
	top: 8px;	
	left: 16px;
	font-size: 16px;
	z-index: 1;
}


.kdsc-logo-container {
    margin-top: 18% !important;
}

.label-container {
    margin-top: 20px; /* Adjusts the gap between the image and the label */
}

.styled-label {
    font-weight: bold !important;
    color: white !important;
    font-size: 24px !important;
	}

.login-image {
	width: 50%;
	height: 50%;
}

.kerala-logo {
   margin-top: 8%;
	width: 25%;
	height: 25%;
}

.space {
	width: 25%;
	height: 25%;
}


.versionInfoBox {
		background-color: #298F60;
		color:#fff;
		margin: 5px;
		padding: 3px;
		margin-top: 8%;
		-moz-border-radius: 5px;
		-webkit-border-radius: 5px;
		font-size: 8pt;
	}
	.login-left-info{
		font-size:24px;
		font-weight:bold;
		overflow-wrap: break-word;
	}
	.login-left-div{
		width:100%;
	}
