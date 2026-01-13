.mandatory-decorator-text {
	text-decoration: none; font-size: xx-small; vertical-align: top;color:#FF3C49 !important;;
}

.editor-box {
	display: inline-block;
	border: none; 
	padding: 0px; 
	margin: 0px; 
	background-color: transparent;
	position: relative;
}

.editor-input {
	box-sizing: border-box;
	-moz-box-sizing: border-box; /* Firefox */
	display: inline-block;
	/*padding-right: 22px; */
	width: 100%;
	height: 36px;
	border-radius: 8px;
	background: #EFF6FF;
}

.editor-input input{
	padding: 7px 16px;
	border:1px solid #EFF6FF;
	border-radius: 8px;
	background: #EFF6FF;
	height:100%;
}

.editor-input.mobile.z-decimalbox {
	/*padding-right: 5px;*/
}

.editor-input:focus {
	/*border: 1px solid #0000ff;*/
	  box-shadow:none !important;
	  -webkit-box-shadow:none !important;
}

.editor-input-disd {
	padding-right: 0px !important;
}
	
.editor-button {
	padding: 0px;
	margin: 0px;
	display: inline-block;
	background-color: transparent;
	background-image: none;
	border: none;
	position: absolute;
	width: 22px;
    height: 24px;
    min-height: 24px;
    right: 0px;
	top: 0px;
}

.editor-button :hover {
	-webkit-filter: contrast(1.5);
	filter: contrast(150%);
}

.editor-button img {
	vertical-align: top;
	text-align: left;
	width: 18px;
	height: 18px;
	padding: 1px 1px;
}


.editor-box .grid-editor-input.z-textbox {
	background:#EFF6FF;
	padding:8px;
}

.editor-box .z-textbox{
	background:#EFF6FF; !important;
	padding:8px;
}


.grid-editor-button {
}

.grid-editor-button img {
}

.number-box {
	display: inline-block; 
	white-space:nowrap;
	border:none;
	border-radius: 8px;
	background: #EFF6FF;
}

.number-box .grid-editor-input.z-decimalbox {
	border-radius: 8px;
	background: #EFF6FF;
}

.datetime-box {
	white-space:nowrap;
}
.datetime-box .z-datebox {
	border-radius: 8px;
	background: #EFF6FF;
}
.datetime-box .z-timebox {
	border-radius: 8px;
	background: #EFF6FF;
}

span.grid-combobox-editor {
	width: 100% !important;
	position: relative;
}

.grid-combobox-editor input {
	width: 100% !important;
	/*padding-right: 26px;*/
	border-bottom-right-radius: 6px;
	border-top-right-radius: 6px;
	border-right: 0px;
	border-radius: 8px;
	background: #EFF6FF;
}

.grid-combobox-editor.z-combobox-disabled input {
	/*border-bottom-right-radius: 3px;
	border-top-right-radius: 3px;
	border-right: 1px solid #F8FAFC;
	padding-right: 5px;*/
	border-radius: 8px;
	border:none !important;
	background: #F8FAFC;
}

.grid-combobox-editor .z-combobox-button {
	position: absolute;
	right: 0px;
	/*border-bottom-right-radius: 3px;
	border-top-right-radius: 3px;
	border-bottom-left-radius: 0px;
	border-top-left-radius: 0px;*/
}

.grid-combobox-editor input:focus {
	/*border-right: 0px;*/
	 box-shadow:none !important;
	  -webkit-box-shadow:none !important;
}
	
.grid-combobox-editor input:focus + .z-combobox-button {
	/*border-left: 1px solid #0000ff;*/
	 box-shadow:none !important;
	  -webkit-box-shadow:none !important;
}

.editor-input.z-combobox + .editor-button {
	background-color: #EFF6FF;
	width: 22px;
    height: 24px;
    min-height: 24px;
    right: 6px;
    top: 6px;
    border: none;
    border-radius: 0;
}
.editor-input.z-combobox > .z-combobox-input {
	border-radius: 8px;
	background: #EAF4EF;
}

.editor-input .z-combobox-input:focus{
 	box-shadow:none !important;
	-webkit-box-shadow:none !important;
}

<%-- payment rule --%>
.payment-rule-editor {
	display: inline-block;
	border: none; 
	padding: 0px; 
	margin: 0px; 
	height: 36px;
	background-color: transparent;
	position: relative;
}
.payment-rule-editor .z-combobox {
	width: 100%;
	height: 100%;
	border: none;
	border-radius: 8px;
	background: #EFF6FF;
}
.payment-rule-editor .z-combobox-input {
	display: inline-block;
	/*padding-right: 44px; */
	width: 100%;
	height: 100%;
	/*border-bottom-right-radius: 6px;
	border-top-right-radius: 6px;
	border-right: 0px;*/
	border: none;
	border-radius: 8px;
	background: #EAF4EF;
}

.payment-rule-editor a{
	background: none;
    border: none;
   	right: 6px;
    top: 6px;
}

.payment-rule-editor .z-combobox-input:focus {
	/*border: 1px solid #0000ff;*/
	 box-shadow:none !important;
	  -webkit-box-shadow:none !important;
}
.payment-rule-editor .z-combobox-input.editor-input-disd {
	/*padding-right: 22px !important;*/
}
.payment-rule-editor .z-combobox-button {
	position: absolute;
	/*right: 0px;*/
}
.payment-rule-editor .z-combobox .z-combobox-button-hover {
	background-color: #ddd;
	background-position: 0px 0px;
}
.payment-rule-editor .editor-button {
	background-color: #EFF6FF;
	width: 22px;
    height: 24px;
    min-height: 24px;
    right: 6px;
    top: 6px;
    border: none;
    border-radius: 0;
}

<%-- chart --%>
.chart-field {
	padding: 10px; 
	border: 1px solid red !important;
}

.field-label {
	position: relative; 
	float: right;
}

.image-field {
	cursor: pointer;
	border: 1px solid #C5C5C5;
	height: 24px;
	width: 24px;
}
.image-field.image-field-readonly {
	cursor: default;
	border: none;
}
.image-fit-contain {
	object-fit: contain;
}
.z-cell.image-field-cell {
	z-index: 1;
}

.html-field {
	cursor: pointer;
	/*border: 1px solid #C5C5C5;*/
	overflow: auto;
}

.dashboard-field-panel.z-panel, .dashboard-field-panel.z-panel > .z-panel-body,  .dashboard-field-panel.z-panel > .z-panel-body > .z-panelchildren  {
	overflow: visible;
}

.idempiere-mandatory, .idempiere-mandatory input, .idempiere-mandatory a {
    border-color:red;
    border-radius:8px;
}


.idempiere-label {
    color: #333;
}

.idempiere-mandatory-label{
   color: red!important;
}

.idempiere-zoomable-label {
    cursor: pointer; 
    text-decoration: underline;
}

/*wms overrides*/

.z-combobox {
	height:36px;
}

.z-combobox-input {
	color: black !important;
	height:100%;
	padding:8px;
	border-radius: 8px;
	border:1px solid #EFF6FF;
	background: #EAF4EF;
}

.z-combobox-icon {
	color: #8F95B2;
}

.z-combobox-input:focus {
	box-shadow: none !important;
	background: #EAF4EF;
}

.z-textbox {
	/*height:100%;*/
	border:1px solid #EFF6FF;
	border-radius: 8px;
	padding:8px;
	background: #EAF4EF;
}

.z-combobox > a {
	background: none;
    border: none;
   	right: 6px;
    top: 6px;
}

.z-datebox {
	height: 36px;
}

.z-datebox-input {
	height: 100%;
	border-radius:8px;
	padding:8px;
	background-color: #EFF6FF;
	background: #EAF4EF;
	border:1px solid #EFF6FF;
}

.z-datebox > a {
	background: none;
	border: none;
	top: 6px;
	right: 6px;
}

.editor-box .editor-button {
	background-color: transparent;
	background-image: none;
	border: none;
	position: absolute;
    right: 6px;
	top: 6px;
}
