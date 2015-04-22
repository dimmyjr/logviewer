com_logviewer_ui_component_Tail = function() {

	var e = this.getElement();

	e.innerHTML = "<div style='position: relative;' tabindex='-1' class='v-panel-content v-scrollable'> "
                          " <div id='' class='v-label v-widget logcontent v-label-logcontent v-has-width'> "
                            " <pre></pre> "
                          " </div> "
                          "</div>";

	this.onStateChange = function() {
	    add(this.getState().value);
	}

	this.add = function (value){
	    return e.getElementsByTagName("pre")[0].innerHTML += value;
	}

	this.clear = function (value){
        return e.getElementsByTagName("pre")[0].innerHTML = '';
    }

	this.scroll = function(){
	     e.scrollTop = e.scrollHeight;
	}

}