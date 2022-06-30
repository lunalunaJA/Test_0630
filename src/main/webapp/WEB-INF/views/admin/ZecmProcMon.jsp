<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['PROCESS_MON']" /></title>
<link rel="stylesheet" href="${css}/reset.css">
<link rel="stylesheet" href="${css}/common.css">
<link rel="stylesheet" href="${css}/style.css">
<link rel="stylesheet" href="${js}/jquery-ui-1.13.1/jquery-ui.min.css">

<script src="${js}/jquery-1.12.2.min.js"></script>
<script src="${js}/jquery-ui-1.13.1/jquery-ui.min.js"></script>
<script src="${js}/jquery.noty.packaged.min.js"></script>
<script src="${js}/jquery-ui.js"></script>
<script src="${js}/common.js"></script>
<script src="${js}/charts5/index.js"></script>
<script src="${js}/charts5/xy.js"></script>
<script src="${js}/charts5/themes/Animated.js"></script>

<script type="text/javascript">
var ctxRoot = '${ctxRoot}';	
var root = '${ctxRoot}';

var cpuChart  = {
	series: {},
	cpuData : [],
	create : function(){
		var cpuRoot = am5.Root.new("cpuChartdiv", {
			useSafeResolution: false
		});
		cpuRoot.fps = 5;
	
		cpuRoot.setThemes([
			am5themes_Animated.new(cpuRoot)
		]);
	
		var chart = cpuRoot.container.children.push(am5xy.XYChart.new(cpuRoot, {
			panX: true,
			panY: true,
			wheelX: "panX",
			wheelY: "zoomX",
			pinchZoomX:true
		}));
	
		var cursor = chart.set("cursor", am5xy.XYCursor.new(cpuRoot, {
			behavior: "none"
		}));
		
		cursor.lineY.set("visible", false);
	
		var xAxis = chart.xAxes.push(am5xy.DateAxis.new(cpuRoot, {
			maxDeviation: 0.2,
			baseInterval: {
				timeUnit: "millisecond",
				count: 1
			},
			renderer: am5xy.AxisRendererX.new(cpuRoot, {}),
			tooltip: am5.Tooltip.new(cpuRoot, {})
		}));
	
		var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(cpuRoot, {
			renderer: am5xy.AxisRendererY.new(cpuRoot, {}),
			max : 100,
			min : 0
		}));
	
		this.series = chart.series.push(am5xy.LineSeries.new(cpuRoot, {
			name: "Series",
			xAxis: xAxis,
			yAxis: yAxis,
			valueYField: "value",
			valueXField: "date",
			tooltip: am5.Tooltip.new(cpuRoot, {
				labelText: "{valueY}"
			})
		}));
	
		this.series.appear(1200);
		chart.appear(100, 100);	
		var that = this;
	
	},
	loadData : function(data){
		var rdata = this.createCpuData(data);
		this.series.data.setAll(rdata);
	},
	createCpuData : function(data){
		this.cpuData.push({
		date:  new Date().getTime(),
		value: Number(data.cpuUsagePercent)
		});
		//데이터는 60개만 유지
		if(this.cpuData.length>60)this.cpuData.shift();
		return this.cpuData;
	}
}

var memChart  = {
	series: {},
	chart : {},
	memData : [],
	memRoot : {},
	xAxis : {},
	yAxis : {},
	no : 0,
	create : function(){
	
		this.memRoot = am5.Root.new("memChartdiv", {
			useSafeResolution: false
		});
		this.memRoot.fps = 5;
		this.memRoot.setThemes([
			am5themes_Animated.new(this.memRoot)
		]);
	
	
		this.chart = this.memRoot.container.children.push(am5xy.XYChart.new(this.memRoot, {
			panX: true,
			panY: true,
			wheelX: "panX",
			wheelY: "zoomX",
			pinchZoomX:true
		}));					
	
	
		var cursor = this.chart.set("cursor", am5xy.XYCursor.new(this.memRoot, {
			behavior: "none"
		}));
		cursor.lineY.set("visible", false);				
	
		this.xAxis = this.chart.xAxes.push(am5xy.CategoryAxis.new(this.memRoot, {
			categoryField: "checkTime",
			startLocation: 0.5,
			endLocation: 0.5,
			renderer: am5xy.AxisRendererX.new(this.memRoot, {}),
			tooltip: am5.Tooltip.new(this.memRoot, {})
		}));
	
		this.yAxis = this.chart.yAxes.push(am5xy.ValueAxis.new(this.memRoot, {
			renderer: am5xy.AxisRendererY.new(this.memRoot, {})
		}));
	
	
		this.chart.appear(100, 100);
		
		this.createSeries("Used", "used");
		this.createSeries("Free", "free");
		//this.createSeries("Total", "total");			

	},
	createSeries : function(name, field){
		var series = this.chart.series.push(am5xy.LineSeries.new(this.memRoot, {
			name: name,
			xAxis: this.xAxis,
			yAxis: this.yAxis,
			stacked:true,
			valueYField: field,
			categoryXField: "checkTime",
			tooltip: am5.Tooltip.new(this.memRoot, {
				pointerOrientation: "horizontal",
				labelText: "[bold]{name}[/]\n{categoryX}: {valueY}"
			})
		}));
		
		series.fills.template.setAll({
			fillOpacity: 0.5,
			visible: true
		});
		series.appear(100);
		
		this.series[name] = series;						 
	},
	loadData : function(data){
		var rdata = this.createMemData(data);					
		this.xAxis.data.setAll(rdata);					
		this.series["Used"].data.setAll(rdata);
		this.series["Free"].data.setAll(rdata);	
		//this.series["Total"].data.setAll(rdata);
	 },
	createMemData : function(data){
		this.memData.push({
			checkTime :  data.checkTime+this.no++,
			// total: Number(data.totalMemory),
			used: Number(data.usedMemory),
			free: Number(data.freeMemory)
		});	
		if(this.memData.length>60)this.memData.shift();
			return this.memData;
	}
}

//서버 자원 조회 및 갱신
var getSystemInfo = function(){
	var that = this;
	$.ajax({
		type : 'POST',
		url : '${ctxRoot}/api/status/cpuMemory',
		dataType : 'json',
		contentType : 'application/json',
		async : false,
		data : JSON.stringify({}),
		success : function(data){
			if(data.status=="0000"){
				//차트 데이터 갱신
				cpuChart.loadData(data.resObj);
				memChart.loadData(data.resObj);				
			}						
		}, error : function(request, status, error) {
		
		}					
	});
}

am5.ready(function() {	
	//차트 생성
	cpuChart.create();
	memChart.create();			

	//2초 주기로 데이터 갱신
	setInterval(function () {				
		getSystemInfo();
	}, 2000);
});

//라이센스 적용
if(am5.addLicense){			
	am5.addLicense("am5c.lic_am5c.lic_am5c.lic_am5c.lic-am5c.lic_am5c.lic");
}

</script>
</head>
<body>

	<!--header stard-->
	<c:import url="../common/TopPage.jsp" />
	<!--header end-->
	<main>
		<div class="flx">
			<c:import url="../common/AdminLeftMenu.jsp" />
		
			<section id="content">
				<div class="innerWrap" style="overflow:auto;">
					<div class="full-content">
						<h2 class="pageTit"><img src="${image}/icon/icon_b01.png" alt=""><spring:eval expression="@${lang}['PROCESS_MON']" /></h2>
						<div class="wdt100">
							<h3 class="innerTit">CPU</h3>
							<div>
								<div id="cpuChartdiv" class="tb_list_stnew_2 sticky-table  table_h3" style="height:440px;">					
								</div>
							</div>
						</div>
						<div class="wdt100">
							<h3 class="innerTit">Memory(JVM)</h3>
							<div>
								<div id="memChartdiv" class="tb_list_stnew_2 sticky-table  table_h3" style="width:*;height:440px;">					
								</div>				
							</div>
						</div>
					</div>
				</div><!--innerWrap//-->
			</section>
		</div>
	</main>
</body>
</html>





