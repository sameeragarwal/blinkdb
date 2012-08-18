var ajax_example = {
   init: function() {
      // Grab the elements we'll need.
      ajax_example.form = document.getElementById('ajax_example');
      ajax_example.results_div = document.getElementById('results');
      
      // This is so we can fade it in later.
      YAHOO.util.Dom.setStyle(ajax_example.results_div, 'opacity', 0);
      
      // Hijack the form.
      YAHOO.util.Event.addListener(ajax_example.form, 'submit', ajax_example.submit_func);
   },
   
   submit_func: function(e) {
      YAHOO.util.Event.preventDefault(e);
      
      // Remove any error messages being displayed.
      var form_fields = ajax_example.form.getElementsByTagName('dd');
      for(var i=0; i<form_fields.length; i++) {
	 if(YAHOO.util.Dom.hasClass(form_fields[i], 'error')) {
	       ajax_example.form.getElementsByTagName('dl')[0].removeChild(form_fields[i]);
	 }
      }
      YAHOO.util.Connect.setForm(ajax_example.form);
      
      //Temporarily disable the form.
      for(var i=0; i<ajax_example.form.elements.length; i++) {
	 ajax_example.form.elements[i].disabled = true;
      }
      var cObj = YAHOO.util.Connect.asyncRequest('POST', '/examples/ajax/1/?xhr', ajax_example.ajax_callback);
   },
   
   ajax_callback: {
      success: function(o) {
	 // This turns the JSON string into a JavaScript object.
	 var response_obj = eval('(' + o.responseText + ')');
	 
	 // Set up the animation on the results div.
	 var result_fade_out = new YAHOO.util.Anim(ajax_example.results_div, {
						      opacity: { to: 0 }
						   }, 0.25, YAHOO.util.Easing.easeOut);

	 if(response_obj.errors) { // The form had errors.
	    result_fade_out.onComplete.subscribe(function() {
						    ajax_example.results_div.innerHTML = '';
						    ajax_example.display_errors(response_obj.errors);
						 });
	 } else if(response_obj.success) { // The form went through successfully.
	    var success_message = document.createElement('p');
	    success_message.innerHTML = 'Form submitted successfully! Submitted input:';
	    var input_list = document.createElement('ul');
	    var name_item = document.createElement('li');
	    name_item.innerHTML = 'Name: ' + response_obj.name;
	    input_list.appendChild(name_item);
	    var total_item = document.createElement('li');
	    total_item.innerHTML = 'Total: ' + response_obj.total;
	    input_list.appendChild(total_item);
	    YAHOO.util.Dom.setStyle(ajax_example.results_div, 'display', 'block');
	    var result_fade_in = new YAHOO.util.Anim(ajax_example.results_div, {
							opacity: { to: 1 }
						     }, 0.25, YAHOO.util.Easing.easeIn);
	    result_fade_out.onComplete.subscribe(function() {
						    ajax_example.results_div.innerHTML = '';
						    ajax_example.results_div.appendChild(success_message);
						    ajax_example.results_div.appendChild(input_list);
						    result_fade_in.animate();
						 });
	 }
	 result_fade_out.onComplete.subscribe(function() {
						 //Re -enable the form.
						 for(var i=0; i<ajax_example.form.elements.length; i++) {
						    ajax_example.form.elements[i].disabled = false;
						 }});
	 result_fade_out.animate();
      },
      
      failure: function(o) { // In this example, we shouldn't ever go down this path.
	 alert('An error has occurred');
      }
   },
   
   display_errors: function(error_obj) {
      for(var err in error_obj) {
	 var field_container = document.getElementById(err + '_container');
	 var error_dd = document.createElement('dd');
	 YAHOO.util.Dom.addClass(error_dd, 'error');
	 error_dd.innerHTML = '<strong>'  + error_obj[err] + '</strong>';
	 YAHOO.util.Dom.setStyle(error_dd, 'opacity', 0);
	 var error_fade_in = new YAHOO.util.Anim(error_dd, {
						    opacity: { to: 1 }
						 }, 0.25, YAHOO.util.Easing.easeIn);
	 field_container.parentNode.insertBefore(error_dd, field_container);
	 error_fade_in.animate();
      }
   }
};

YAHOO.util.Event.addListener(window, 'load', ajax_example.init);