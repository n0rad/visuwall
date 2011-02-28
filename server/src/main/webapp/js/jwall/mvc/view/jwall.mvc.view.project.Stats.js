jwall.mvc.view.Stats = {

	TestStatus : function(data, divId) {
		var fill = pv.colors("red", "yellow", "green"),
		    fillText = pv.colors("black", "white", "red"),
		    w = 600,
		    h = 30,
		    x = pv.Scale.linear(0, 100).range(0, w),
		    y = pv.Scale.ordinal(pv.range(1)).splitBanded(0, h, 1);

		var vis = new pv.Panel()
			.canvas(divId)
		    .width(w)
		    .height(h);

		var bar = vis.add(pv.Layout.Stack)
		    .layers(data)
		    .orient("left-top")
		    .x(function() { return y(this.index); })
		    .y(x)
		  .layer.add(pv.Bar)
			.fillStyle(fill.by(pv.parent))
		    .height(y.range().band);

		bar.anchor("right").add(pv.Label)
		    .visible(function(d) { return d > .2; })
		    .textStyle(fillText.by(pv.parent))
		    .text(function(d) { return d.toFixed(1); } );

		vis.render();		
	}	
};