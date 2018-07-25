AJS.$(document).ready(function () {
	
	//Add some 15 minutes delay to not missclick and plan as much as possible
	var delay = 15;
	var today = new Date();
	today.setMinutes(today.getMinutes() + delay);
	var defaultTime = today.toTimeString().substring(0,5);
	
	//Parametrize Timepicker
	var options = {
			now: defaultTime, //hh:mm 24 hour format only, defaults to current time
			twentyFour: true,
	        title: 'Selectionner heures & minutes', //The Wickedpicker's title,
	        showSeconds: false, //Whether or not to show seconds,
	        timeSeparator: ':', // The string to put in between hours and minutes (and seconds)
	        secondsInterval: 1, //Change interval for seconds, defaults to 1,
	        minutesInterval: 1, //Change interval for minutes, defaults to 1
	        clearable: false //Make the picker's input clearable (has clickable "x")
	    };
    $('.timepicker').wickedpicker(options);
    $('.wickedpicker').css("z-index","4000");
    
	var today = new Date();
	var	oneMonthMaxDate = new Date();
	oneMonthMaxDate.setMonth(today.getMonth()+1);
	
	var formatFr = "dd/mm/yy";
	var datePickerParam = {'overrideBrowserDefault': true, 'languageCode' : 'fr','dateFormat': formatFr};
	var expectedFormatDate = formatFr;
	
	// Hides the dialog
	AJS.$("#edit-close-button").click(function(e) {
	    e.preventDefault();
	    AJS.$("#edit-form-error").hide();
	    AJS.dialog2("#edit-dialog").hide();
	});
	
	AJS.$("#edit-loading").hide();
	AJS.$("#add-loading").hide();
	AJS.$("#delete-loading").hide();
	AJS.$("#unactivate-loading").hide();
	AJS.$("#activate-loading").hide();
	AJS.$("#cancel-loading").hide();
	
	AJS.$('#delete-application-date').datePicker( datePickerParam	);
    AJS.$('#add-application-date').datePicker( datePickerParam	);
    AJS.$('#unactivate-application-date').datePicker( datePickerParam	);
    AJS.$('#activate-application-date').datePicker( datePickerParam	);
    $('#delete-application-date-time').wickedpicker();
    $('#add-application-date-time').wickedpicker();
    $('#unactivate-application-date-time').wickedpicker();
    $('#edit-application-date-time').wickedpicker();
    $('#activate-application-date-time').wickedpicker();

    AJS.$('#delete-application-date').attr("min",formatDate(today, expectedFormatDate));
    AJS.$('#add-application-date').attr("min",formatDate(today, expectedFormatDate));
    AJS.$('#unactivate-application-date').attr("min",formatDate(today,expectedFormatDate));
    AJS.$('#activate-application-date').attr("min",formatDate(today,expectedFormatDate));
    AJS.$('#delete-application-date').attr("max",formatDate(oneMonthMaxDate,expectedFormatDate));
    AJS.$('#add-application-date').attr("max",formatDate(oneMonthMaxDate,expectedFormatDate));
    AJS.$('#unactivate-application-date').attr("max",formatDate(oneMonthMaxDate,expectedFormatDate));
    AJS.$('#activate-application-date').attr("max",formatDate(oneMonthMaxDate,expectedFormatDate));
    
    AJS.$("#btn-add-customsecurity-lvl").click(function(e) {
    	e.preventDefault();
        AJS.dialog2("#add-dialog").show();
    });
    
    AJS.$("#add-rule-active").change(function() {
    	var required = false;
        if(this.checked) {
        	AJS.$("#add-application-date-section").show();
        	required = true;
        }
        else{
        	AJS.$("#add-application-date-section").hide();
        }
        $("#add-application-date").prop('required',required);
    });
	
	
    AJS.$("#add-application-date-section").hide();
    AJS.$("#add-security-lvl").auiSelect2();
    AJS.$("#edit-security-lvl").auiSelect2();
    AJS.$("#add-events").auiSelect2();
    AJS.$("#edit-events").auiSelect2();
    AJS.$("#layout").auiSelect2();
    
    
    AJS.$("#cancel-form").on("submit", function(e){
    	
    	e.preventDefault();
		var param = {};
		param.id = $("#cancel-rule-id").val();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule/cancel",
			  type: "POST",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				 AJS.dialog2("#cancel-dialog").hide();
				 AJS.$("#cancel-loading").hide();
				$("#message").val("csl.admin.securityrule.cancel.success");
				$("#messageRuleName").val($("#cancel-rule-name").val());
				$("#actionMessage").submit();
			});
			request.fail(function( data ) {
				AJS.$("#cancel-form-error").show();
				AJS.$("#cancel-loading").hide();
				var jsonResponse = JSON.parse(data.responseText);
				var errorMessage = jsonResponse.error;
				AJS.$("#cancel-form-error > p:nth-child(2)").text(errorMessage);
			});
		
    });
    AJS.$("#add-form").on("submit", function(e){
    	e.preventDefault();
		var param = {};
		var addApplicationDate = new Date( $.datepicker.parseDate('dd/mm/yy', AJS.$('#add-application-date').val()));
		var expectedDateFormat = $.datepicker.formatDate( "yy-mm-dd", addApplicationDate );
		
		param.jql = $("#add-jql").val();
		param.active= $("#add-rule-active").is(':checked');
		param.securityLvl= $("#add-security-lvl").val();
		param.events = $("#add-events").val();
		param.ruleName = $("#add-rule-name").val();
		param.priority = $("#add-priority").val();
		param.applicationDate = formatDateTime(expectedDateFormat,$("#add-application-date-time").wickedpicker('time'));
		
		AJS.$("#add-form-error").hide();
		AJS.$("#add-loading").show();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule",
			  type: "POST",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				 AJS.dialog2("#add-dialog").hide();
				 AJS.$("#add-loading").hide();
				 $("#message").val("csl.admin.securityrule.add.success");
				 $("#messageRuleName").val(param.ruleName);
				 $("#actionMessage").submit();
			});
			request.fail(function( data ) {
				AJS.$("#add-form-error").show();
				AJS.$("#add-loading").hide();
				var jsonResponse = JSON.parse(data.responseText);
				var errorMessage = jsonResponse.error;
				AJS.$("#add-form-error > p:nth-child(2)").text(errorMessage);
			});
			
		});
    
    
    AJS.$("#edit-form").on("submit", function(e){
    	e.preventDefault();
		var param = {};
		param.id = $("#edit-rule-id").val();
		param.events = $("#edit-events").val();
		param.ruleName = $("#edit-rule-name").val();
		param.priority = $("#edit-priority").val();
		
		AJS.$("#edit-form-error").hide();
		AJS.$("#edit-loading").show();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule",
			  type: "PUT",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				AJS.dialog2("#edit-dialog").hide();
				AJS.$("#edit-loading").hide();
				$("#message").val("csl.admin.securityrule.update.success");
				$("#messageRuleName").val(param.ruleName);
				$("#actionMessage").submit();
			});
			request.fail(function( data ) {
				AJS.$("#edit-form-error").show();
				AJS.$("#edit-loading").hide();
				
				var jsonResponse = JSON.parse(data.responseText);
				var errorMessage = jsonResponse.error;
				AJS.$("#edit-form-error > p:nth-child(2)").text(errorMessage);
				
			
			});
			
		});
    
  
    AJS.$("#delete-form").on("submit", function(e){
    	e.preventDefault();
    	var id =  $("#delete-rule-id").val();
    	var name =  $("#delete-rule-name").val();
		var param = {};
		param.id = id;
		var deleteApplicationDate = new Date( $.datepicker.parseDate('dd/mm/yy', AJS.$('#delete-application-date').val()));
		var expectedDateFormat = $.datepicker.formatDate( "yy-mm-dd", deleteApplicationDate );
		param.applicationDate = formatDateTime(expectedDateFormat,$("#delete-application-date-time").wickedpicker('time'));
	
		
		AJS.$("#delete-form-error").addClass('hide');
		AJS.$("#delete-loading").show();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule",
			  type: "DELETE",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				AJS.$("div[id=rule-" + id + "]" ).remove();
				alertUser("sucess-messages", AJS.I18n.getText("csl.admin.securityrule.delete.success.msg"));
				var countRule = AJS.$("div[id^=rule-]" ).size();
				if(countRule == 0){
					AJS.$("#no-rule-message").show();
				}
				AJS.dialog2("#delete-dialog").hide();
				$("#message").val("csl.admin.securityrule.update.success");
				$("#messageRuleName").val(name);
				$("#actionMessage").submit();
			});
			
    });
    
    AJS.$("#unactivate-form").on("submit", function(e){
    	e.preventDefault();
    	var id =  $("#unactivate-rule-id").val();
    	var name =  $("#unactivate-rule-name").val();
		var param = {};
		param.id = id;
		
		var unactivateApplicationDate = new Date( $.datepicker.parseDate('dd/mm/yy', AJS.$('#unactivate-application-date').val()));
		var expectedDateFormat = $.datepicker.formatDate( "yy-mm-dd", unactivateApplicationDate );
		param.applicationDate = formatDateTime(expectedDateFormat,$("#unactivate-application-date-time").wickedpicker('time'));
		
		AJS.$("#unactivate-form-error").addClass('hide');
		AJS.$("#unactivate-loading").show();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule/unactivate",
			  type: "POST",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				AJS.$("div[id=rule-" + id + "]" ).remove();
				alertUser("sucess-messages", AJS.I18n.getText("csl.admin.securityrule.unactivate.success.msg"));
				AJS.dialog2("#unactivate-dialog").hide();
				$("#message").val("csl.admin.securityrule.unactivate.success");
				$("#messageRuleName").val(name);
				$("#actionMessage").submit();
			});
			
    });
    
    AJS.$("#activate-form").on("submit", function(e){
    	e.preventDefault();
    	console.log("Activation!");
    	var id =  $("#activate-rule-id").val();
    	var name =  $("#activate-rule-name").val();
		var param = {};
		param.id = id;
		
		var activateApplicationDate = new Date( $.datepicker.parseDate('dd/mm/yy', AJS.$('#activate-application-date').val()));
		var expectedDateFormat = $.datepicker.formatDate( "yy-mm-dd", activateApplicationDate );
		param.applicationDate = formatDateTime(expectedDateFormat,$("#activate-application-date-time").wickedpicker('time'));
		
		AJS.$("#activate-form-error").addClass('hide');
		AJS.$("#activate-loading").show();
		
		var request = AJS.$.ajax({
			  url:  AJS.contextPath() + "/rest/csl/1.0/security-rule/activate",
			  type: "POST",
			  data: JSON.stringify(param),
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				AJS.$("div[id=rule-" + id + "]" ).remove();
				alertUser("sucess-messages", AJS.I18n.getText("csl.admin.securityrule.activate.success.msg"));
				AJS.dialog2("#activate-dialog").hide();
				$("#message").val("csl.admin.securityrule.activate.success");
				$("#messageRuleName").val(name);
				$("#actionMessage").submit();
			});
			
    });
    
    
    AJS.$("[id^=delete-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	var name = AJS.$(this).attr('data-name');
    	console.log("Delete name : " + name);
    	AJS.$("#delete-rule-name").attr("value",name)
    	AJS.$("#delete-rule-id").attr("value",id);
    	AJS.dialog2("#delete-dialog").show();
	});
    
    
    AJS.$("[id^=unactivate-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	var name = AJS.$(this).attr('data-name');
    	AJS.$("#unactivate-rule-name").attr("value",name)
    	AJS.$("#unactivate-rule-id").attr("value",id);
    	AJS.dialog2("#unactivate-dialog").show();
    });
    
    AJS.$("[id^=activate-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	var name = AJS.$(this).attr('data-name');
    	AJS.$("#activate-rule-name").attr("value",name)
    	AJS.$("#activate-rule-id").attr("value",id);
    	AJS.dialog2("#activate-dialog").show();
    });
    
    AJS.$("[id^=cancel-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	var name = AJS.$(this).attr('data-name');
    	AJS.$("#cancel-rule-id").attr("value",id);
    	AJS.$("input#cancel-rule-name").attr("value",name);
    	AJS.$("span#cancel-rule-name").text(name);
    	AJS.dialog2("#cancel-dialog").show();
    });
    
    
    AJS.$("[id^=edit-sr]").on("click", function(e){
    	var id = AJS.$(this).attr('data-id');
    	var editApplicationDate  = AJS.$(this).attr('data-edit-date-application');
    	var editApplicationTime  = AJS.$(this).attr('data-edit-time-application');
    	var name = AJS.$(this).attr('data-name');
    	var priority = AJS.$(this).attr('data-priority');
    	var jql = AJS.$(this).attr('data-jql');
    	var securitylvl =AJS.$(this).attr('data-security-lvl');
    	var events = AJS.$(this).attr('data-events');
    	var eventsAsArray = JSON.parse(events);
    	var active =AJS.$(this).attr('data-active');
    	var activeAsBoolean = (active == 'true');
    	
    	 AJS.$("#edit-rule-id").attr("value",id);
    	 AJS.$("#edit-priority").attr("value",priority);
    	 AJS.$("#edit-rule-name").attr("value",name);
    	 AJS.$("#edit-jql").attr("value",jql);
    	 AJS.$("#edit-security-lvl").attr("value",securitylvl);
    	 AJS.$("#edit-rule-active").prop("checked",activeAsBoolean);
    	 AJS.$("#edit-application-date").attr("value",editApplicationDate);
    	 AJS.$("#edit-application-date-time").attr("value",editApplicationTime);
    	 
    	 AJS.$('#edit-security-lvl').val(securitylvl);
    	 AJS.$('#edit-security-lvl').trigger('change.select2');
    	 
    	 AJS.$('#edit-events').val(eventsAsArray);
    	 AJS.$('#edit-events').trigger('change.select2');
    	 
    	AJS.dialog2("#edit-dialog").show();
    });
    function alertUser(idElement, msg ){
    	AJS.$("#" + idElement).show();
		 AJS.$("#" + idElement + " > p:nth-child(2)").text(msg);
		 window.setTimeout(function(){
			 AJS.$("#"+ idElement).hide();
		 }, 5000);
    }
    function addNewrule(location){
    	console.log("GET " + location);
    	var request = AJS.$.ajax({
			  url:  AJS.contextPath() + location,
			  type: "GET",
		      contentType: "application/json",
			  dataType: "json"
			});
			request.success(function( data ) {
				console.data;
			});
    }
    function formatDate(date,expectedFormatDate) {
    	 var separator = '-';

    	  var day = date.getDate();
    	  var month = date.getMonth() + 1;
    	  var year = date.getFullYear();
    	  var hour = date.getHours();
    	  var minute = date.getMinutes();
    	  if( month <= 9){
    		  month = "0" + month; 
    	  }
    	  var ret = year + separator + month + separator + day;
    	  if( expectedFormatDate == 'dd/mm/yy'){
      		separator = '/';
      		ret = day + separator + month + separator + year; 
      	}
  
    	  return ret;
    	}
    function formatDateTime(date, time){
    	return date + "T" + time;
    }
    function loadTooltip(){
    	console("Load tooltip!");
    	AJS.$("#" + this.id ).tooltip();
    }
    
    window.addEventListener("load", function(event) {
        AJS.$(".tooltiped").each(function(index, elem){
        	AJS.$("#" + this.id ).tooltip();
        });
  });
});

    