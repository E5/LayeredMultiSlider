LayeredMultiSlider{

/*****************************************************
collision.ist@virgin.net

	_version:			060708
					Code from Fredrik
					
	_version:			060609
	_req.classes:		Workspace

*****************************************************/ 
	classvar values;

	var w_sliders;
      
	// this is a normal constructor method
	*new {|containerView, width= 300, height= 100, numSliders= 16,
			numLayers= 4, colours, layerNames|

		^super.new.init(containerView, width, height, numSliders, numLayers, colours, layerNames) 
	}

	init {| containerView, width, height, numSliders, numLayers, colours, layerNames |
	     // do initiation here
		var w_mainComp, /*w_sliders,*/ w_layerSelector, selectedLayer= 3,
			layerColours, w_selectComp, clipboard, w_mute, w_solo, w_zero,
			w_rand, w_copy, w_paste;
		
		values= Array.fill(numLayers, {Array.fill(numSliders, {1.0.rand})});
		
		colours = colours ?? {Array.fill(numLayers, {Color.rand})};
		layerNames = layerNames ?? {Array.fill(numLayers, {|i| ("Layer"+i).asString})};
	
		layerColours= Array.fill(colours.size);
		w_sliders= Array.fill(numLayers+1); 
		w_selectComp= Array.fill(numLayers); 
		w_layerSelector= Array.fill(numLayers); 
		w_mute= Array.fill(numLayers); 
		w_solo= Array.fill(numLayers); 
		w_zero= Array.fill(numLayers); 
		w_rand= Array.fill(numLayers); 
		w_copy= Array.fill(numLayers); 
		w_paste= Array.fill(numLayers); 
	
		// initialise the "layerColours" array
		colours.do{|colour, i|
			var fadedColour, brighterColour;
			fadedColour= Color.fromArray(colour.asArray).alpha_(0.75);
			brighterColour= Color.fromArray(colour.asArray).brighter(0.3);
			layerColours[i]= [fadedColour, colour, brighterColour]
		};
		
//		w_mainComp= SCCompositeView(containerView, (width + 8) @ (height + 32))
		w_mainComp= SCCompositeView(containerView, Rect(0, 0, (width + 8), (height + 32)))
			.background_(Color.clear);
		w_mainComp.decorator= FlowLayout(w_mainComp.bounds);
	
		numLayers.do {|i|
			// draw the SCMultiSliderViews
			w_sliders[i]= SCMultiSliderView(w_mainComp, width @ height)
				.background_(Color.clear)
				.isFilled_(false)
				.xOffset_(4)
				.thumbSize_(((w_mainComp.bounds.width - 6) / numSliders) - 4)
				.valueThumbSize_(8)
				.strokeColor_(Color.clear)
				.fillColor_(layerColours[i][0])
				.drawLines_(false)
				.drawRects_(true)
				.gap_(4)
				.editable_(false);
		
			w_mainComp.decorator.reset;
		
			w_sliders[i].value_(values[i]);
		};
		
		w_sliders[numLayers-1].isFilled_(true);
		
		// draw the invisible control slider
		w_sliders[numLayers]= SCMultiSliderView(w_mainComp, width @ height)
			.background_(Color.clear)
			.isFilled_(false)
			.xOffset_(4)
			.thumbSize_(((width + 2) / numSliders) - 4)
			.valueThumbSize_(8)
			.strokeColor_(Color.clear)
			.fillColor_(Color.clear)
			.drawLines_(false)
			.drawRects_(true)
			.editable_(true)
			.action_({|me|
				values[selectedLayer][me.index]= me.currentvalue;
				w_sliders[selectedLayer].value_(values[selectedLayer]);
			});
		
		w_mainComp.decorator.shift(0, 3);
	
		// select a layer with this
		w_layerSelector= SCPopUpMenu(w_mainComp, 80 @ 14)
			.items_(layerNames)
			.font_(Workspace.tinyFontTight)
			.background_(Color.white)
			.action_({|me|
				selectedLayer= me.value;
	
				numLayers.do{|i|
					if (i != selectedLayer, {
						// hide other comps
						w_selectComp[i].visible_(false);
						w_sliders[i]
							.fillColor_(layerColours[i][0])
							.strokeColor_(Color.clear)
					})
				};
	
				w_selectComp[selectedLayer]
					.background_(layerColours[selectedLayer][1])
					.visible_(true);
	
				w_sliders[selectedLayer]
					.fillColor_(layerColours[selectedLayer][1])
					.strokeColor_(Color.black);
			});
		
		numLayers.do{|i|
			w_mainComp.decorator.reset;
			w_mainComp.decorator.shift(84, height+4);
			w_selectComp[i]= SCCompositeView(w_mainComp, (width-84) @ 20)
				.background_(layerColours[i][1])
				.visible_(false);
			w_selectComp[i].decorator= FlowLayout(w_selectComp[i].bounds);
			
	
			// visual "mute" button
			w_mute[i]= SCButton(w_selectComp[i], 39 @ 13)
				.states_([
					["mute", Color.black, Color.grey(0.7)],
					["mute", Color.white, Color.blue]
				])
				.font_(Workspace.tinyFontTight)
				.action_({|me|
					if( me.value == 1, {
						w_sliders[i].visible_(false);
					},{
						w_sliders[i].visible_(true);
					});
				});
	
			// visual "solo" button
			w_solo[i]= SCButton(w_selectComp[i], 39 @ 13)
				.states_([
					["solo", Color.black, Color.grey(0.7)],
					["solo", Color.white, Color.red]
				])
				.font_(Workspace.tinyFontTight)
				.action_({|me|
					if (me.value == 1, {
						numLayers.do{|j| if(i != j, {w_sliders[j].visible_(false);}) }
					},{
						numLayers.do{|j| w_sliders[j].visible_(true); }
					});
				});
	
			// zero all button
			w_zero[i]= SCButton(w_selectComp[i], 39 @ 13)
				.states_([["zero", Color.black, Color.rand]])
				.font_(Workspace.tinyFontTight)
				.action_({|me|
					values[i]= Array.fill(numSliders, 0);
					w_sliders[i].value_(values[i]);
				});
	
			// rand button
			w_rand[i]= SCButton(w_selectComp[i], 39 @ 13)
				.states_([["rand", Color.black, Color.rand]])
				.font_(Workspace.tinyFontTight)
				.action_({|me|
					values[i]= Array.fill(numSliders, {1.0.rand});
					w_sliders[i].value_(values[i]);
				});
	
			// copy button
			w_copy[i]= SCButton(w_selectComp[i], 39 @ 13)
				.states_([["copy", Color.black, Color.rand]])
				.font_(Workspace.tinyFontTight)
				.action_({|me|
					clipboard= values[i];
				});
	
			// paste button
			w_paste[i]= SCButton(w_selectComp[i], 39 @ 13)
				.states_([["paste", Color.black, Color.rand]])
				.font_(Workspace.tinyFontTight)
				.action_({|me|
					clipboard.notNil.if(
						w_sliders[i].value_(clipboard);
						values[i]= clipboard;
					)
				});
		};
		
		// initialise the selected layer multislider
		w_sliders[selectedLayer]
			.fillColor_(layerColours[selectedLayer][1])
			.strokeColor_(Color.black);
	
		// initialise the "w_layerSelector"
		w_layerSelector.valueAction_(selectedLayer);
	}
	
	value {|layer, slider|
		^values[layer][slider]
	}

	valueArray {|layer|
		^values[layer]
	}

	value_ {|layer, slider, val|
		values[layer].put(slider, val);
		w_sliders[layer].value_(values[layer])
	}

	valueArray_ {|layer, val|
		values[layer]= val;
		w_sliders[layer].value_(values[layer])
	}
//	valueAction_ {|layer, slider, val|
//		values[layer].put(slider, val);
//		w_sliders[layer].value_(values[layer])
//	}

}
