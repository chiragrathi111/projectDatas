<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.idempiere.org/dsp/web/util" prefix="u" %>
.menu-treecell-cnt {
	border: 0; margin: 0; padding: 0;
	${fontFamilyC};
	${fontSizeM}; 
	font-weight: normal;
    overflow-x: hidden;
    white-space: nowrap;
    text-overflow: ellipsis !important;
    cursor: pointer;
}

.menu-treecell-cnt .z-treecell-content{
	display: inline-flex;
    align-items: center;
    gap: 8px;
}

.menu-treecell-cnt .z-treecell-content{
	display: inline-flex;
    align-items: center;
    gap: 8px;
}

.menu-treecell-cnt .z-treecell-content .menu-href{
	padding: 0px 16px !important;
}

div.z-tree-body td.menu-tree-cell {
	cursor: pointer;
	padding: 0 2px;
   	${fontSizeM};
   	font-weight: normal;
   	overflow: visible;
}

div.menu-tree-cell-cnt {
	border: 0; margin: 0; padding: 0;
	${fontFamilyC};
	${fontSizeM}; 
	font-weight: normal;
    white-space:nowrap
}

td.menu-tree-cell-disd * {
	color: #C5CACB !important; cursor: default!important;
}

td.menu-tree-cell-disd a:visited, td.menu-tree-cell-disd a:hover {
	text-decoration: none !important;
	cursor: default !important;;
	border-color: #D0DEF0 !important;
}

div.z-dottree-body td.menu-tree-cell {
	cursor: pointer; padding: 0 2px;
	${fontSizeM}; 
	font-weight: normal; overflow: visible;
}

div.z-filetree-body td.menu-tree-cell {
	cursor: pointer; padding: 0 2px;
	${fontSizeM}; 
	font-weight: normal; overflow: visible;
}

div.z-vfiletree-body td.menu-tree-cell {
	cursor: pointer; padding: 0 2px;
	${fontSizeM}; 
	font-weight: normal; overflow: visible;
}

.z-treerow:hover>.z-treecell {
	background: #EAF4EF !important;
}

.z-treerow.z-treerow-selected>.z-treecell {
	background: #298F60 !important;
}
