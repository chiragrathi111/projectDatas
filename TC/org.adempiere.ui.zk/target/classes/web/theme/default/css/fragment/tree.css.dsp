<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-treecell-content {
	${fontFamilyC};
	${fontSizeM};
}
.tree-moveitem-btn {
	padding: 2px 4px; 
	border-radius: 3px;
}
.tree-moveitem-btn.pressed {
	box-shadow: inset 0 0 0 1px #efefef,inset 0 3px 15px #9f9f9f;
}

.z-treerow > .z-treecell > .z-treecell-content > .z-tree-line.z-tree-spacer:first-child {
	width:0px;
}
.z-treerow > .z-treecell > .z-treecell-content > .z-tree-line.z-tree-spacer + .z-tree-icon {
	width: 30px;
}
.z-treerow > .z-treecell > .z-treecell-content > .z-tree-line.z-tree-spacer + .z-tree-icon::before {
	content: " ";
	display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    vertical-align: middle;
}

/*WMS overrides*/

.recentitems-box .menu-href{
   	display: flex;
   	align-items: center;
   	margin-left: 22px;
    padding: 8px;
    gap: 8px;
    flex-shrink: 0;
   /* color: #64748B !important;*/
    font-weight: 500;
}
.recentitems-box .menu-href:hover {
   text-decoration: none !important;
   background: #EAF4EF !important;
}

.recentitems-box .menu-href i{
	font-size: 20px;
	font-weight: 500;
	color: #64748B !important;
}

.fav-tree-panel .z-treecell-content {
	/*padding: 10px 3px !important
	 color: #64748B !important;*/
	display: flex;
	align-items: center;
    gap: 16px;
    flex-shrink: 0; 
}

.fav-tree-panel .z-treecell-content .z-treecell-text{
	color: #64748B;
	font-style: normal;
	font-weight: 500;
	line-height: 160%;
	letter-spacing: 0.2px;
	/*padding-left: 20px;*/
}

.fav-tree-panel .z-treecell-content .z-treecell-text i{
	font-size: 20px;
}

.fav-tree-panel .z-toolbarbutton-content i {
	font-size: 16px;
}

.fav-tree-panel .z-treecell-content{
	padding: 8px;
}

.fav-tree-panel .z-treecell-content i{
	/*margin-right: 16px;*/
	width:24px;
}

.fav-tree-panel .z-treecell-text {
	display: flex;
	gap:16px;
}

.fav-tree-panel .z-treecell-content a{
	position: absolute;
	right: 0;
	color: #64748B;
}

.fav-tree-panel .z-treecell-content a  .z-toolbarbutton-content{
	color: #64748B !important;
}

.fav-tree-panel .z-treecell-text {
	display: flex;
    align-items: center;
}

/*wms overrides*/
.z-toolbarbutton:hover {
	border: 1px solid transparent;
   	background: none !important;
    box-shadow: none !important;
    outline: none !important;
}

.z-a {
	color: #298F60;
}
