com_logviewer_ui_component_TailView = function() {

	var e = this.getElement();
	var scroll = false;

	e.innerHTML = "<pre></pre>";

	this.onStateChange = function() {
		scroll = this.getState().scroll;

		if (this.getState().value){
			this.add(this.getState().value);
		} else {
			this.setText(this.getState().text);
		}
	}

	this.setText = function (value){
		e.getElementsByTagName("pre")[0].innerHTML = value;
		this.scroll();
	}

	this.add = function (value){
	    e.getElementsByTagName("pre")[0].innerHTML += value;
	    this.scroll();
	}

	this.clear = function (value){
        e.getElementsByTagName("pre")[0].innerHTML = '';
    }

	this.scroll = function(){
		 if (scroll == true) {
			window.setInterval(function() {
			  e.parentNode.scrollTop = e.parentNode.scrollHeight;
			}, 5000);
		}
	}

}