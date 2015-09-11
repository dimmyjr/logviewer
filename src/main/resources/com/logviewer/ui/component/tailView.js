com_logviewer_ui_component_TailView = function() {

	var e = this.getElement();
	var scroll = false;

	e.innerHTML = "";
	var content = e;

	this.onStateChange = function() {
		scroll = this.getState().scroll;

		if (this.getState().value){
			this.add(this.getState().value);
		} else {
			this.setText(this.getState().text);
		}
	}

	this.setText = function (value){
		content.innerHTML = value;
		this.scroll();
	}

	this.add = function (value){
	    content.innerHTML += value;
	    this.scroll();
	}

	this.clear = function (value){
        content.innerHTML = '';
    }

	this.scroll = function(){
		 if (scroll == true) {
			window.setInterval(function() {
				if (scroll == true) {
			  		e.parentNode.scrollTop = e.parentNode.scrollHeight;
			  	}
			}, 5000);
		}
	}

}