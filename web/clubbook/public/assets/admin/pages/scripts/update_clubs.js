
var ComponentsPickers = function () {


    var handleTimePickers = function () {

        if (jQuery().timepicker) {

            $('.timepicker-no-seconds').timepicker({
                autoclose: true,
                minuteStep: 5,
                showSeconds: false,
                showMeridian: false
            });

            // handle input group button click
            $('.timepicker').parent('.input-group').on('click', '.input-group-btn', function(e){
                e.preventDefault();
                $(this).parent('.input-group').find('.timepicker').timepicker('showWidget');
            });
        }
    }

    return {
        //main function to initiate the module
        init: function () {
            handleTimePickers();
        }
    };

}();


var FormValidation = function () {

    // validation using icons
    var handleValidation2 = function() {
        // for more info visit the official plugin documentation: 
            // http://docs.jquery.com/Plugins/Validation

            console.log('Validation')

            var form = $('#form_update_club');
            var error2 = $('.alert-danger', form);
            var success2 = $('.alert-success', form);

            

            form.validate({
                errorElement: 'span', //default input error message container
                errorClass: 'help-block help-block-error', // default input error message class
                focusInvalid: false, // do not focus the last invalid input
                ignore: "",  // validate all fields including form hidden input
                rules: {
                    club_name: {
                      minlength: 2,
                      required: true
                    },
                    club_email: {
                      required: true,
                      email: true
                    },
                    club_site: {
                      required: true
                    },
                    club_phone: {
                      required: true
                    },
                    club_info: {
                      required: true, 
                    },
                    club_logo :{
                      required: true, 
                    },
                    club_images: {
                      required: true,  
                    },
                    club_capacity: {
                      required: true
                    },
                    club_address: {
                      required: true
                    }

                },

                invalidHandler: function (event, validator) { //display error alert on form submit  
                    console.log('invalidHandler');
                    success2.hide();
                    error2.show();
                    Metronic.scrollTo(error2, -200);
                },

                errorPlacement: function (error, element) { // render error placement for each input type
                    console.log('errorPlacement');
                    console.log(error);
                    var icon = $(element).parent('.input-icon').children('i');
                    icon.removeClass('fa-check').addClass("fa-warning");  
                    icon.attr("data-original-title", error.text()).tooltip({'container': 'body'});
                },

                highlight: function (element) { // hightlight error inputs
                    console.log('highlight');
                    $(element)
                        .closest('.form-group').removeClass("has-success").addClass('has-error'); // set error class to the control group   
                },

                unhighlight: function (element) { // revert the change done by hightlight
                    
                },

                success: function (label, element) {
                    console.log("success");
                    console.log(label);
                    var icon = $(element).parent('.input-icon').children('i');
                    $(element).closest('.form-group').removeClass('has-error').addClass('has-success'); // set success class to the control group
                    icon.removeClass("fa-warning").addClass("fa-check");
                },

                submitHandler: function (form) {
                    success2.show();
                    error2.hide();
                    form.submit();
                }
            });


    }



    return {
        //main function to initiate the module
        init: function () {
            handleValidation2();
        }

    };

}();