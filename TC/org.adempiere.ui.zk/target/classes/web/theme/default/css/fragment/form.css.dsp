.form-label 
{
	text-align: right;
}

.form-label-heading
{
	text-align: center;
}

.form-button {
	width: 99%;
}


.adwindow-form .form-button {
	display: flex;
    width: 381px;
    height: 24px;
    padding: 16px;
    justify-content: center;
    align-items: center;
    gap: 8px;
    flex-shrink: 0;
    border-radius: 8px;
    border: 1px solid var(--primary, #298F60);
    background: #F8FAFC;
    color: var(--gray-500, #64748);
}

.adwindow-form .form-button i{
	 color: var(--gray-500, #64748);
}

.form-button img {
	width: 16px;
	height: 16px;
}

<%-- confirm panel --%>
.confirm-panel {
	width: auto;
	height: auto;
	position: relative;
	padding-left: 2px;
	padding-right: 2px;
}
.confirm-panel-right {
	float: right;
}
.confirm-panel-left {
	float: left;
}
.confirm-panel-center{
	padding-left: 5px;
	float: left;
}

<%-- busy dialog --%>
.busy-dialog {
	cursor: wait;
	background-color: transparent;
}
.busy-dialog-box {
	background-color: transparent;
	padding: 9px; 
}
.busy-dialog-img {
	height: 16px; 
	width: 16px;
	background: transparent no-repeat center;
	background-image: url(../images/progress3.gif) !important;
	display: -moz-inline-box; 
	vertical-align: top; 
	display: inline-block;
}
.busy-dialog-label {
	color: #363636;
}

<%-- status bar --%>
.status {
	width: 100%;
	height: 29px;
}
.status-db {
    white-space: nowrap;
	padding-top: 0;
	padding-bottom: 0;
	padding-left: 5px;
	padding-right: 5px;
	cursor: pointer;
	width: 100%;
	height: 100%;
	margin: 0;
	border-left: solid 1px #9CBDFF;
}
.status-info {
	padding-right: 10px;
	border-left: solid 1px #9CBDFF;
}

.status-selected{
	padding-right: 5px;
	padding-right: 5px;
	border-right: solid 1px #9CBDFF;
}

.status-border {
	border: solid 1px #9CBDFF;
}

<%-- report wizard --%>
.report-wizard-footer {
	width: 100%;
}

<%-- wms overrides--%>

.login-btn{
	width:100%;
	background:#298F60 !important;
	color:#fff !important;
	padding: 8px;
	border-radius: 8px;
	border: 1px solid #298F60;
	background: rgba(37, 99, 235, 0.10);
}

.login-box-footer .z-hlayout-inner {
	width: inherit;
}

.login-confirm-panel-btn{
	width: inherit;
}

.wms-role-confirm-pannel {
	width: 100%;
}

.wms-role-confirm-pannel div {
	width: 100%;
}

.adwindow-form .number-box input{
	padding-right:36px;
}


