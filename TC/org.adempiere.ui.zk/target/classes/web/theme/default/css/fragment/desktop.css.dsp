<%-- header --%>

.desktop-header-left {
	/*
	margin: 0;
	margin-left: 1px;
	margin-top: 1px;
	background-color: transparent !important; 
	border: none !important;
	*/	
}

.desktop-header-right {
	margin: 0;
	margin-top: 1px;
	padding-right:1px;
	background-color: transparent !important; 
	border: none !important;
}

.desktop-header {
	/*background-color: #E4E4E4;*/
	width: 100%;
	height: 46px;
	/*border-bottom: 1px solid #C5C5C5 !important;*/	
	padding-left: 4px;	
}

.desktop-header-logo-img {
	width: 100px;
	height: 40px;
}

.desktop-header-logo-imgB {
	margin-left: 5px !important;
	width: 100px;
	height: 40px;
}

.desktop-header.mobile {
	height: 36px;
	padding: 4px 4px;
}

.desktop-header-font {
	font-size: 10px;
}

.desktop-header-popup {
	width: 800px;
	border-radius: 2px;
	border: 1px solid #d5d5d5;
	border-right: 2px solid #d5d5d5;
	border-bottom-width: 2px;
	right: 1px;
}

.desktop-header-popup .desktop-header {
	border: none;
	height: 100% !important;
}

.desktop-header-username {
    padding-right: 6px;
    color: #6F767E;
}

.desktop-header-username:hover {
	color: #298F60;
	text-decoration: none;
}

.desktop-user-panel {
	float: right;
}
.desktop-user-panel.mobile .desktop-header-font.desktop-header-username {
	font-weight: 300;
	clear: both;
	display: inline-block;
	overflow: hidden;
	white-space: nowrap;
}

.desktop-layout {
	position: absolute; 
	border: none;
	background-color: #E4E4E4;
}

.desktop-tabbox {
	padding-top: 0px; 
	/*	background-color: #E4E4E4;*/	
	background-color:#fff;
}

.desktop-tabbox .z-tabs-content {
	background: #F8FAFC;
	padding-left: 32px;
	/*border-bottom: 1px solid #F8FAFC;*/
}

.desktop-tabbox .z-tab {
/*	height: 24px;*/
}

@media screen and (max-width: 360px) {
	.desktop-tabbox .z-tab {
		max-width: 190px;
	}
}

.desktop-tabbox .z-tab-selected {
	/*border-top: 2px solid #666;
	border-top-left-radius: 5px 5px;
	border-top-right-radius: 5px 5px;*/
	border-bottom: 2px solid #298F60 !important;	
	border-top: none;	
    border-right: none;	
    border-left: none;	
    /*color:#2563EB !important;*/
}

.desktop-tabbox .z-tab-selected .z-tab-text {
	/*padding-top: 0px;*/
	padding-bottom: 0px;
	color:#298F60 !important;	
	background: #F8FAFC;	
}

.desktop-tabbox .z-tab-button {	
	background: #F8FAFC;
}

.desktop-tabbox .z-tab-selected .z-tab-button  {
	color:#298F60 !important
}
.desktop-tabbox > .z-tabpanels {
	flex-grow: 1 1 0;
}

.desktop-north  {
	height: 84px !important;
}

.desktop-north-include{
	display:flex !important;
	align-items: center !important;
}

.desktop-center {
	border: none;
	padding-top: 1px;
	background: #F8FAFC;
	top:86px !important;
}

.desktop-center .z-tab{
	background: #F8FAFC;
	border-top: none;
    border-right: none;
    border-left: none;
    border-bottom: 1px solid #E2E8F0;
    height: 32px;
}

.desktop-center .z-tab .z-tab-text{
	color:#64748B;
	font-weight: 700;
	background: #F8FAFC;
}

.desktop-tabpanel {
	margin: 0;
	padding: 0;
	border: 0;
	position: relative !important;
	background-color: #FFFFFF;
}

.desktop-left-column {
	width: 200px;
	border: none;
	border-right: 1px solid #C5C5C5;
	/*background-color: #E4E4E4;*/
	background-color: #FFF;
	padding-top: 2px; 
	position:relative;
	top:86px !important;
}

.menu-look-up{
	margin-left: 122px;
    display: flex;
}

.desktop-right-column {
	width: 200px;
	border: none;
	border-left: 1px solid #C5C5C5;
	background-color: #E4E4E4;
	padding-top: 2px; 
}

.desktop-left-column + .z-west-splitter,  .desktop-left-column.z-west {
	border-top: none; 
	/*border-right: 1px solid #c5c5c5;*/
	border-right: none;
}

.desktop-right-column + .z-east-splitter,  .desktop-right-column.z-east {
	border-top: none; 
	border-left: 1px solid #c5c5c5;
}

.desktop-left-column .z-west-body {
	border-right: none;
}

.desktop-right-column .z-east-body {
	border-left: none;
}

.desktop-layout > div > .z-west-collapsed, .desktop-layout > div > .z-east-collapsed {
	border-top: none;
}

.desktop-left-column .z-anchorlayout, .desktop-right-column .z-anchorlayout {
	overflow-x: hidden;
}

.z-anchorlayout { overflow:auto }
 
.z-anchorchildren { overflow:visible }

.desktop-hometab {
}

.desktop-tabbox .z-tabs .z-toolbar-tabs-body {
	padding: 0px !important;
	margin: 0px !important;
}

.desktop-tabbox .z-tabs .z-toolbar-tabs-body .z-toolbarbutton {
	padding: 0px !important;
	border: 1px solid transparent !important;
	margin: 0px !important;
}

.desktop-tabbox .z-tabs .z-toolbar-tabs .z-toolbarbutton-hover {
	border: none !important;
	padding: 0px !important;
	margin: 0px !important;
}

.desktop-tabbox .z-tabs .z-toolbar-tabs .z-toolbarbutton-hover .z-toolbarbutton-content {
	background-image: none !important;
	background-color:#DDD !important;
	padding: 0px !important;
	margin: 0px !important;
	-webkit-box-shadow:inset 0px 0px 3px #CCC;
	-moz-box-shadow:inset 0px 0px 3px #CCC;	
	-o-box-shadow:inset 0px 0px 3px #CCC;	
	-ms-box-shadow:inset 0px 0px 3px #CCC;	
	box-shadow:inset 0px 0px 3px #CCC;
}

.desktop-menu-popup {
	z-index: 9999;
	background-color: #fff;
}

.desktop-menu-toolbar {
	background-color: #ffffff; 
	verticle-align: middle; 
	padding: 2px;
	border-top: 1px solid #c5c5c5;
}

.desktop-home-tabpanel {
	background-color: #F8FAFC;
	width: 100% !important;
}

.link {
	cursor:pointer;
	padding: 2px 2px 4px 4px;
	border: none !important;
}

.link.z-toolbarbutton:hover {
	border-bottom: none !important;
	background-image: none !important;
	text-decoration: none;
}

.link.z-toolbarbutton:hover span {
	color: #298F60;
}

.desktop-home-tabpanel .z-panel-head {
	background-color: #FFFFFF;
}

<%-- window container --%>
.window-container-toolbar-btn.z-toolbarbutton .z-toolbarbutton-content img {
	width: 16px;
	height: 16px;
	padding: 3px 3px;
}
.window-container-toolbar-btn.z-toolbarbutton .z-toolbarbutton-content [class^="z-icon"] {
	width: 22px;
	height: 22px;
	padding: 3px 3px;
}
.window-container-toolbar > .z-toolbar-content,
.window-container-toolbar-btn.z-toolbarbutton, 
.window-container-toolbar-btn.z-toolbarbutton .z-toolbarbutton-content,
.window-container-toolbar-btn.z-toolbarbutton .z-toolbarbutton-content [class^="z-icon"] {
	display:inline-flex;
	align-items: center;
	justify-content: center;
	border-bottom: 0px; 
}

.user-panel-popup .z-popup-content {
	padding-left: 0px;
	padding-right: 0px;
}
.user-panel-popup .z-popup-content > .z-vlayout {
	overflow-x: auto;
	padding: 8px;
}

.dashboard-content-help .z-popup-content {
	background: black;
	color: white;
	border-radius: 5px;
}

.z-menuitem.selected .z-menuitem-text {
	font-weight: bold;
}

.window-container-toolbar-btn.tab-list {
	font-size: smaller;
	padding-right: 6px;
}
.window-container-toolbar-btn.tab-list i {
	padding-right: 0px;
	margin-right: -4px;
	font-size: larger;
}

/*wms overrides*/

.desktop-header-left .z-button {
	background:none;
	border:none;
}

.desktop-header-left .z-button i{
	color: #28303F;
}

.desktop-header-left .z-image{
	margin-left:32px;
}

.dashboard-widget-remove-header .z-panel-head {
	display: none;
}

.dashboard-widget img {
    width: 24px;
    height: 24px;
}

.dashboard-widget .z-caption-content {
	display : flex;
	align-items : center;
	gap: 16px;	
} 

.dashboard-widget .z-caption {
	display: flex;
    justify-content: space-between;
} 


.dashboard-btn {
	display: flex !important;
    align-items: center;
    gap: 16px;
    font-weight: 700;
    margin-left: 3% !important;
}

.dashboard-btn:focus {
	background : none;
	border: none;
	box-shadow : none;
}

.dashboard-btn:hover {
	background : none;
	border: none;
}

.desktop-tabpanel .z-listbox-header {
	background : blue;
}

.desktop-tabpanel table tr th {
	border: none;
	background : blue;
}

.desktop-tabpanel table tr th .z-listheader-content {
	color: #FFF !important;
}

.desktop-tabpanel table .z-listbox-odd {
  	background: #F1F5F9;
}

.desktop-tabpanel table .z-listitem-selected {
	background: #bbdefb;
}

.desktop-tabpanel table tr td  {
  	background: inherit !important;
}

.z-listheader-checkable {
	display: inline-block;
   	width: 20px;
	height: 20px;
    border: 1px solid #64748B;
   	border-radius: 4px;
    background: #F8F8F8;
    vertical-align: text-top;
}

.z-listitem-checkbox {
	display: inline-block;
   	width: 20px;
	height: 20px;
    border: 1px solid #64748B;
    border-radius: 4px;
    background: #F8F8F8;
    vertical-align: text-top;
}

.z-listheader-checkable.z-listheader-checked .z-listheader-icon {
	color: #2184BA;
    display: block;
    /* padding-left: 0px; */
    padding-top: 2px;
    line-height: 14px;
}

.z-listitem-selected>.z-listcell>.z-listcell-content>.z-listitem-checkable .z-listitem-icon {
	color: #2184BA;
    display: block;
    /* padding-left: 0px; */
    padding-top: 2px;
    line-height: 14px;
}

.number-box .editor-input {
	padding-right : 36px;
}
