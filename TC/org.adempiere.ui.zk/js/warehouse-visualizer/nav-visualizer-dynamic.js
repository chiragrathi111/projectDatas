var draw;
var menuDraw;

var WHLayout = {
  cfg: {
    viewBox: {
      width: "100%",
      height: "800",
    },
    defaultWHGutterWidth: 20,
    defaultWHWidth: 200,
    defaultWHHeight: 100,
    defaultBinWidth: 20,
    defaultBinGutterWidth: 10,
    defaultRackSpacing: 40,
    zones: [
      {
        name: "Default",
        color: "#F8F7FC",
        occupancyColors: [],
        coords: { x: 0, y: 0 },
        width: 600,
        height: 600,
        aisles: 7,
        aisleCfg: {
          labelFn: "alphabetical",
          levelsPerSide: 5,
          binsPerLevel: 20,
        },
      },
      {
        name: "Dispatch",
        color: "#f6edfa",
        occupancyColors: [],
        coords: { x: 640, y: 0 },
        width: 400,
        height: 400,
        aisles: 5,
        aisleCfg: {
          labelFn: "alphabetical",
          levelsPerSide: 5,
          binsPerLevel: 20,
        },
      },
      {
        name: "Cold Storage",
        color: "#edeffa",
        occupancyColors: [],
        coords: { x: 640, y: 410 },
        width: 800,
        height: 200,
        aisles: 3,
        aisleCfg: {
          labelFn: "alphabetical",
          levelsPerSide: 5,
          binsPerLevel: 40,
        },
      },
    ],
  },
  viewBox: {
    coords: { x: 0, y: 0 },
    width: 0,
    height: 0,
  },
};

var occupancy = [0, 20, 40, 60, 80, 100];
var COLORS = ["#BCF4A4", "#aba446", "#f5f55f", "#f0c560", "#f79a36", "#FC1907"];
var BINCOLORS = [
  "#BCF4A4",
  "#FC1907",
  "#aba446",
  "#f5f55f",
  "#f0c560",
  "#f79a36",
];

var origBinColor, origWHColor;
var colorsBand = generateColorRange("#BCF4A4", "#FC1907", 100);

var occupancyMatrix;

function hexToRgb(hex) {
  var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return result
    ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16),
      }
    : null;
}

function generateColorRange(start, end, steps) {
  var startRGB = hexToRgb(start),
    endRGB = hexToRgb(end);

  var colors = [];

  for (i = 0; i <= steps; i++) {
    if (startRGB.r < endRGB.r) {
      startRGB.r += 1;
    } else if (startRGB.r > endRGB.r) {
      startRGB.r -= 1;
    }
    if (startRGB.g < endRGB.g) {
      startRGB.g += 1;
    } else if (startRGB.g > endRGB.g) {
      startRGB.g -= 1;
    }
    if (startRGB.b < endRGB.b) {
      startRGB.b += 1;
    } else if (startRGB.b > endRGB.b) {
      startRGB.b -= 1;
    }
    colors.push(
      "rgb(" + startRGB.r + "," + startRGB.g + "," + startRGB.b + ")",
    );
  }

  return colors;
}

function initWarehouseVis(data) {
  warehouses = data;
  console.log(warehouses);
  draw = SVG()
    .addTo(".topNav")
    .size(WHLayout.cfg.viewBox.width, WHLayout.cfg.viewBox.height);

  WHLayout.viewBox.coords = {
    x: draw.node.clientLeft + 40,
    y: draw.node.clientTop,
  };
  WHLayout.viewBox.height = draw.node.clientHeight;
  WHLayout.viewBox.width = draw.node.clientWidth;

  addTopLevlNav();
}

function onWHClick(e) {
  var wh = e.detail.wh;

  var navArea = document.getElementsByClassName("navigationArea")[0];
  var detailArea = document.getElementsByClassName("detailArea")[0];

  addDetailView(WHLayout.cfg, wh);

  navArea.style.display = "none";
  detailArea.style.display = "block";
}

function onWHEnter(e) {
  var wh = e.detail.wh;

  origWHColor = this.css("fill");

  var occupancyVal = wh.occupancyPercents;

  this.css({ fill: "#000000" });
}

function onWHOut(e) {
  var wh = e.detail.wh;
  //console.log('onWHOut: ', e);
  this.css({ fill: origWHColor });
}

function goBack() {
  var navArea = document.getElementsByClassName("navigationArea")[0];
  var detailArea = document.getElementsByClassName("detailArea")[0];

  navArea.style.display = "block";
  detailArea.style.display = "none";
}

function getColorForOccupancy(occupancy) {
  //here occupancy is index to pick color from colorsband..
  return colorsBand[Math.ceil(occupancy)];
  // return '#bada55';
}

function addTopLevlNav(layout) {
  layout = layout || WHLayout.cfg;

  if (draw) {
    var startX = WHLayout.viewBox.coords.x;
    var startY = WHLayout.viewBox.coords.y + 40;
    warehouses.forEach((z, idx) => {
      var whg = draw.group();

      var wh = draw
        .rect(WHLayout.cfg.defaultWHWidth, WHLayout.cfg.defaultWHHeight)
        .move(
          startX +
            idx *
              (WHLayout.cfg.defaultWHGutterWidth + WHLayout.cfg.defaultWHWidth),
          startY,
        )
        .attr({ fill: z.warehouseTotalQty > 0 ? "#BCF4A4" : "#FC1907", id: id });

        // .attr({ fill: getColorForOccupancy(z.occupancyPercents), id: z.id });
      // var text = draw.text(z.occupancyPercents + "%");
       var text = draw.text(z.warehouseTotalQty.toString());
      console.log(z.warehouseTotalQty);
      // var text = draw.text(50);
      text
        .move(
          20 +
            startX +
            idx *
              (WHLayout.cfg.defaultWHGutterWidth + WHLayout.cfg.defaultWHWidth),
          WHLayout.cfg.defaultWHHeight / 6 + startY,
        )
        .font({ fill: "#000000", family: "Inconsolata", size: 48 });

      var whName = draw
        .rect(WHLayout.cfg.defaultWHWidth, 40)
        .move(
          startX +
            idx *
              (WHLayout.cfg.defaultWHGutterWidth + WHLayout.cfg.defaultWHWidth),
          WHLayout.cfg.defaultWHHeight + startY,
        )
        .attr({ fill: "#f1f1f1", id: z.id });
      var nameText = draw.text(z.warehouseName);
      nameText
        .move(
          20 +
            startX +
            idx *
              (WHLayout.cfg.defaultWHGutterWidth + WHLayout.cfg.defaultWHWidth),
          WHLayout.cfg.defaultWHHeight + startY,
        )
        .font({ fill: "#f06", family: "Inconsolata" });

      whg.add(wh);
      whg.add(text);
      whg.add(whName);
      whg.add(nameText);

      whg.on("whclicked", onWHClick);
      whg.on("whmouseenter", onWHEnter);
      whg.on("whmouseout", onWHOut);

      whg.on("click", function (e) {
        this.fire("whclicked", { wh: z });
      });
      whg.on("mouseenter", function (e) {
        this.fire("whmouseenter", { wh: z });
      });
      whg.on("mouseout", function (e) {
        this.fire("whmouseout", { wh: z });
      });
    });
  } else {
    //console.log('unexpected draw value. Nothing to draw');
  }
}

function addDetailView(layout, wh) {
  console.log("hello warehouse am here,", wh);
  var header = document.getElementsByClassName("whSelectionHeader")[0];
  header.textContent = wh.name;

  draw = SVG()
    .addTo(".layoutArea")
    .size(WHLayout.cfg.viewBox.width, WHLayout.cfg.viewBox.height);

  WHLayout.viewBox.coords = { x: draw.node.clientLeft, y: draw.node.clientTop };
  WHLayout.viewBox.height = draw.node.clientHeight;
  WHLayout.viewBox.width = draw.node.clientWidth;

  layout = layout || WHLayout.cfg;

  if (draw) {
    var zones = wh.locations;
    var zoneYposition = 0;
    var areaOccupiedByZone = 0;
    zones.forEach((z, idx) => {
      if (
        z.locatorTypeName !== "Dispatch" &&
        z.locatorTypeName !== "Receiving"
      ) {
        var zoneYObj = calcZoneEndYPosition(z, zoneYposition);

        // zone rectangle box
        //  draw
        //     .rect(200, zoneYObj.zoneHeight)
        //     .move(0, zoneYposition)
        //    .attr({ fill: "#f6edfa" });

        //zone label
        var zoneLbl = draw.text(z.locatorTypeName);
        zoneLbl
          .move(100, zoneYposition + 10)
          .font({ fill: "#f06", family: "Inconsolata" });

        // draw.rect(100,30).move(0,zoneYposition).attr({ fill:'#F8F7FC',});

        zoneYposition = addZoneToLayout(z, zoneYposition);
        areaOccupiedByZone = zoneYObj.currRackY;

        draw.rect(100, 30).move(0, zoneYposition).attr({ fill: "#FFF" });
      }
    });
    // addLegend(1100, 100);
  } else {
    //console.log('unexpected draw value. Nothing to draw');
  }
}

function getFormattedLocators(locators) {
  var aisles = {};

  // example value
  // aisels = {
  // 	'A': {
  // 		'L': {
  // 			'1': [1, 2, 3]
  // 		},
  // 		'R': {
  // 			'1': [1, 2, 3]
  // 		}
  // 	}
  // }

  var keys = Object.keys(locators);

  keys.forEach((l) => {
    var arr = l.split("-"); //A-L-4-12
    var aisle = arr[0];
    var side = arr[1];
    var level = arr[2];
    var bin = arr[3];

    if (!aisles[aisle]) {
      aisles[aisle] = {};
      aisles[aisle][side] = {};
      aisles[aisle][side][level] = [];
    }

    if (!aisles[aisle][side] || Object.keys(aisles[aisle][side]).length == 0) {
      aisles[aisle][side] = {};
      aisles[aisle][side][level] = [];
    }

    if (!aisles[aisle][side][level]) {
      aisles[aisle][side][level] = [];
    }

    aisles[aisle][side][level].push(bin);
  });
  console.log("aisles", aisles);
  return aisles;
}

function getOccupancyMatrix(locators) {
  var matrix = {};

  // example value
  //
  // matrix = {'A-L': [[true, true, false, false, true], [false, false, true, true]]}

  var entries = Object.entries(locators);

  entries.forEach((e) => {
    var arr = e[0].split("-");
    var aisle = arr[0];
    var side = arr[1];
    var level = arr[2];
    var bin = arr[3] * 1 - 1;

    var key = aisle + "-" + side;

    if (!matrix[key]) {
      matrix[key] = [];
    }

    if (!matrix[key][bin]) {
      matrix[key][bin] = [];
    }

    matrix[key][bin].splice(level * 1 - 1, 0, e[1]);
  });

  return matrix;
}

//TODO: number of levels assumed to be 5
function getCalculatedColorCode(matrix, aisle, level, bin) {
  var levelWiseBinOccupancy = matrix[aisle + "-" + level][bin];

  if (levelWiseBinOccupancy) {
    var occupied = levelWiseBinOccupancy.filter((v) => v == true);
    var free = levelWiseBinOccupancy.filter((v) => !v);

    var val = (100 * occupied.length) / 5;

    var idx = occupancy.findIndex((v) => v == val);

    return BINCOLORS[idx];
  } else {
    return "#000000";
  }
}

function addZoneToLayout(zone, zoneYposition) {
  occupancyMatrix = getOccupancyMatrix(zone.locators);

  var fmtdLocators = getFormattedLocators(zone.locators);

  var aisles = Object.keys(fmtdLocators);

  var currRackX = 20;
  var currRackY = zoneYposition + 40;

  var binW = WHLayout.cfg.defaultBinWidth;
  var binH = binW;

  aisles.forEach((a) => {
    var leftSide = fmtdLocators[a].L;
    var rightSide = fmtdLocators[a].R;

    if (leftSide) {
      var levels = Object.keys(leftSide);

      var binsCnt = leftSide[levels[0]].length;

      var binX = currRackX;
      var binY = currRackY;

      //TODO: this code needs some optimization as it is rendering bins at each layer
      for (var i = 0; i < binsCnt; i++) {
        var color = getCalculatedColorCode(occupancyMatrix, a, "L", i);
        console.log("color of the bins", color);
        var side1bin = draw
          .rect(binW, binH)
          .move(binX, binY)
          .attr({ fill: color, id: "bin-" + a + "-L-" + i });
        binX += binW + WHLayout.cfg.defaultBinGutterWidth;

        // side1bin.on("mouseenter", showMenu);
        // side1bin.on("mouseout", hideMenu);
      }

      //aisle name is here
      draw
        .text(a)
        .move(currRackX, currRackY + binH + WHLayout.cfg.defaultRackSpacing / 4)
        .font({ fill: "#f06", family: "Inconsolata" });
    }

    if (rightSide) {
      var levels = Object.keys(rightSide);
      var binsCnt = rightSide[levels[0]].length;

      var binX = currRackX;
      var binY = currRackY + binH + WHLayout.cfg.defaultRackSpacing;

      //TODO: this code needs some optimization as it is rendering bins at each layer
      for (var i = 0; i < binsCnt; i++) {
        var color = getCalculatedColorCode(occupancyMatrix, a, "R", i);
        var side2bin = draw
          .rect(binW, binH)
          .move(binX, binY)
          .attr({ fill: color, id: "bin-" + a + "-R-" + i });
        binX += binW + WHLayout.cfg.defaultBinGutterWidth;

        // side2bin.on("mouseenter", showMenu);
        // side2bin.on("mouseout", hideMenu);
      }
    }

    currRackY += 2 * binH + WHLayout.cfg.defaultRackSpacing + 2;
  });

  return currRackY;
}

function calcZoneEndYPosition(zone, zoneYposition) {
  var currRackY = zoneYposition + 30; //
  var startYPosition = zoneYposition;
  var fmtdLocators = getFormattedLocators(zone.locators);
  var aisles = Object.keys(fmtdLocators);
  aisles.forEach((a) => {
    var leftSide = fmtdLocators[a].L;
    if (leftSide) {
      currRackY += 30;
    }
    currRackY += 20;
    var rightSide = fmtdLocators[a].R;
    if (rightSide) {
      currRackY += 30;
    }
  });
  var zoneHeight = currRackY - startYPosition;
  console.log("zoneHeight", zoneHeight);
  let refObj = { zoneHeight: zoneHeight, currRackY: currRackY };
  return refObj;
}

function addLegend(x, y) {
  var legendGrp = draw.group().move(x, y);

  draw
    .text("Occupancy Legend")
    .move(x, y - 30)
    .font({ fill: "#000000", family: "Inconsolata" });

  draw.rect(20, 20).move(x, y).attr({ fill: COLORS[0] });
  draw
    .text("- 0%")
    .move(x + 30, y)
    .font({ fill: "#000000", family: "Inconsolata" });

  draw
    .rect(20, 20)
    .move(x, y + 30)
    .attr({ fill: COLORS[1] });
  draw
    .text("- 20%")
    .move(x + 30, y + 30)
    .font({ fill: "#000000", family: "Inconsolata" });

  draw
    .rect(20, 20)
    .move(x, y + 60)
    .attr({ fill: COLORS[2] });
  draw
    .text("- 40%")
    .move(x + 30, y + 60)
    .font({ fill: "#000000", family: "Inconsolata" });

  draw
    .rect(20, 20)
    .move(x, y + 90)
    .attr({ fill: COLORS[3] });
  draw
    .text("- 60%")
    .move(x + 30, y + 90)
    .font({ fill: "#000000", family: "Inconsolata" });

  draw
    .rect(20, 20)
    .move(x, y + 120)
    .attr({ fill: COLORS[4] });
  draw
    .text("- 80%")
    .move(x + 30, y + 120)
    .font({ fill: "#000000", family: "Inconsolata" });

  draw
    .rect(20, 20)
    .move(x, y + 150)
    .attr({ fill: COLORS[5] });
  draw
    .text("- 100%")
    .move(x + 30, y + 150)
    .font({ fill: "#000000", family: "Inconsolata" });
}

function addSensorData(x, y) {
  draw.image("images/temperature.png").size(20, 20).move(x, y);
  draw.text("3°C").move(x + 25, y);

  draw
    .image("images/moisture.png")
    .size(20, 20)
    .move(x + 60, y);
  draw.text("40%").move(x + 60 + 25, y);
}

function addBinMenuContent(occupancyArr) {
  menuDraw = SVG().addTo(".binMenu").size(150, 200);

  var occupied = occupancyArr.filter((v) => v == true);
  var free = occupancyArr.filter((v) => !v);

  var occupancyVal = (100 * occupied.length) / 5;

  var idx = occupancy.findIndex((v) => v == occupancyVal);

  menuDraw.rect(200, 30).attr({ fill: COLORS[idx] });
  menuDraw.text("Occupancy - " + occupancyVal + "%").move(10, 5);

  menuDraw.text("Level 5").move(10, 40);
  menuDraw
    .rect(20, 20)
    .move(60, 40)
    .attr({ fill: occupancyArr[4] ? COLORS[0] : COLORS[5] });

  menuDraw.text("Level 4").move(10, 70);
  menuDraw
    .rect(20, 20)
    .move(60, 70)
    .attr({ fill: occupancyArr[3] ? COLORS[0] : COLORS[5] });

  menuDraw.text("Level 3").move(10, 100);
  menuDraw
    .rect(20, 20)
    .move(60, 100)
    .attr({ fill: occupancyArr[2] ? COLORS[0] : COLORS[5] });

  menuDraw.text("Level 2").move(10, 130);
  menuDraw
    .rect(20, 20)
    .move(60, 130)
    .attr({ fill: occupancyArr[1] ? COLORS[0] : COLORS[5] });

  menuDraw.text("Level 1").move(10, 160);
  menuDraw
    .rect(20, 20)
    .move(60, 160)
    .attr({ fill: occupancyArr[0] ? COLORS[0] : COLORS[5] });
}

function showMenu(e) {
  var id = e.target.id;
  var arr = id.split("-");

  var occupancyVal = occupancyMatrix[arr[1] + "-" + arr[2]][arr[3]];

  origBinColor = this.css("fill");

  this.css({ fill: "#000000" });

  addBinMenuContent(occupancyVal);

  var menuEls = document.getElementsByClassName("binMenu");
  menuEls[0].style.left = e.offsetX + 10 + "px";
  menuEls[0].style.top = e.offsetY + 10 + "px";
  menuEls[0].style.visibility = "visible";
}

function hideMenu(e) {
  this.css({ fill: origBinColor });

  var menuEls = document.getElementsByClassName("binMenu");
  menuEls[0].style.visibility = "hidden";

  if (menuDraw) {
    menuDraw.remove();
  }
}
