/* .z-window.z-window-noborder.z-window-noheader.z-window-embedded
.z-window.z-window-noborder.z-window-noheader.z-window-embedded .z-window-content
twwo css make height of window always 100% its parent,
it fix some issue relate resize, but can make side effect
when detect side effect, fix to only apply for parameter window*/
.z-window.z-window-noborder.z-window-noheader.z-window-embedded,
.z-window.z-window-noborder.z-window-noheader.z-window-embedded .z-window-content,
.main-parameter-layout {
	height: 100%;
}
.process-modal-dialog .main-parameter-layout{
	height: auto;
	flex-basis: auto;
}
.process-modal-dialog.z-window > .z-window-content {
	flex: 1 1 auto;
}

.main-parameter-layout,
.top-parameter-layout,
.bottom-parameter-layout,
.message-parameter-layout,
.input-paramenter-layout,
.option-paramenter-layout,
.report-option-container,
.bottom-container{
	width: 100%;
}

.report-option-container {
	overflow-x: auto;
}

.top-parameter-layout{
	overflow: auto;
	/*padding-bottom: 2vh;*/
	flex-basis: auto;
	margin-bottom: 24px;
}

.bottom-parameter-layout{
	padding: 4px 4px 0px 4px;
	border-top: 1px solid rgba(0, 0, 0, 0.2);
	overflow: visible;
}

.message-paramenter {
	max-height: 300pt; 
	overflow: auto;
	line-height: normal;
}

.option-input-parameter{
	
}

.bottom-container{
	overflow: hidden;
}

.button-container{
	float: right;
	overflow: hidden;
	padding: 4px;
}

.save-parameter-container {
	overflow-x: auto;
	overflow-y: hidden;
	display: flex;
    flex-direction: column;
    gap: 8px;
}

.result-parameter-layout {
	overflow: auto;
}

.popup-dialog.z-window.z-window-overlapped.z-window-shadow,
.popup-dialog.z-window.z-window-noborder.z-window-highlighted.z-window-shadow{
	width:600px;
}
@media screen and (max-width: 600px) {
	.popup-dialog.z-window.z-window-overlapped.z-window-shadow,
	.popup-dialog.z-window.z-window-noborder.z-window-highlighted.z-window-shadow{
		width:100%;
	}
}

.input-paramenter-layout{
	margin-top: 24px;
}
@media screen and (max-width: 700px) {
	.input-paramenter-layout{
		width: 90% !important;
		margin-left: 2% !important;
		margin-right: 8% !important;
	}
}
@media screen and (max-width: 500px) {
	.input-paramenter-layout{
		width: 100% !important;
		margin-left: 0 !important;
		margin-right: 0 !important;
	}
}

.popup-dialog .input-paramenter-layout{
	width: 90%;
}

@media screen and (max-width: 500px) {
	.option-input-parameter.z-label.print-format-label,
	.option-input-parameter.z-label.view-report-label {
		display: none;
	}		
}
@media screen and (min-width: 501px) {
	.option-input-parameter.print-format-list > input::-webkit-input-placeholder {
		color: white;
	}
}
@media screen and (min-width: 501px) {
	.option-input-parameter.print-format-list > input::-moz-placeholder {
		color: white;
	}
}
@media screen and (min-width: 501px) {
	.option-input-parameter.print-format-list > input::-ms-input-placeholder {
		color: white;
	}
}
@media screen and (max-width: 400px) {
	.option-input-parameter.print-format-list {
		width: 180px !important;
	}
}
@media screen and (max-width: 320px) {
	.option-input-parameter.print-format-list {
		width: 150px !important;
	}
}
@media screen and (max-width: 500px) {
	.save-parameter-container .saved-parameter-label {
		display: none;
	}
	
	/*.bottom-parameter-layout .saved-parameter-ctr{
		display: flex !important;
	    gap: 24px  !important;
	    align-items: center  !important;
	    /* justify-content: center  !important;
	} */
	
 	 .saved-parameter-label {
		margin-left: 40px !important;
	}
	
	.saved-parameter-list{
		width: 62%;
	}
}
@media screen and (min-width: 501px) {
	.save-parameter-container .saved-parameter-list > input::-webkit-input-placeholder {
		color: white;
	}
}
@media screen and (min-width: 501px) {
	.save-parameter-container .saved-parameter-list > input::-moz-placeholder {
		color: white;
	}
}
@media screen and (min-width: 501px) {
	.save-parameter-container .saved-parameter-list > input::-ms-input-placeholder {
		color: white;
	}
}

/*wms overrides*/
.save-parameter-container .saved-parameter-label{
/*	margin-left:45px;
	padding-right: 20px;*/
}	

.save-parameter-container .saved-parameter-list{
    width: 64%;
}

.saved-parameter-actions{
    display: flex;
    align-items: center;
    justify-content: end;
    gap: 15px;
    width: 40%;
    padding: 4px 5px;
}

.save-parameter-cont {
	padding-left: 8px;
	display:flex;
	align-items:center;
	gap:22px;
}

.save-parameter-btns-cont {
	display:flex;
	justify-content:center;	
	margin-right: 34%;
}

.saved-parameter-actions button{
	width: 25px;
    height: 8px;
    font-size: smaller !important;
}

.message-paramenter b {
	color: #0F172A !important;
	font-size: 16px !important;
	font-style: normal;
	font-weight: 600;
	line-height: normal;
}

.message-paramenter p {
	padding-top: 8px;
}

.process-modal-dialog .main-parameter-layout{
	padding: 24px 32px;	
}

.process-modal-dialog {
	background-color: #298F60;
}

.info-panel {
	background-color: #298F60;
}

.info-panel table {
	border : none;
}

.info-panel table tr , td  {
	border :none !important;
}

.info-panel table th {
	border :none;
	padding: 8px 0px;
	background: #F8FAFC;
}

.info-panel table th .z-listheader-content {
	color : #94A3B8;
}

.info-panel table .z-listbox-odd {
  	background: #F1F5F9;
}

.info-panel table .z-listitem-selected {
	background: #bbdefb;
}

.info-panel table tr td  {
  	background: inherit !important;
}

.popup-dialog {
	background-color: #298F60;
}






