var TableManaged = function () {

    return {

        //main function to initiate the module
        init: function () {
            
            if (!jQuery().dataTable) {
                return;
            }

         
            // begin first table
            $('#dob_gift_clients_table').dataTable({
                "aoColumns": [
                  { "bSortable": false },
                  null,
                  null,
                  null,
                  null,
                  null
                ],
                "iDisplayLength": -1,
                "bLengthChange": false,
                "bFilter" : false, 
                "bPaginate": false,
                "oLanguage": {
                        "sProcessing":   window.MESSAGES.sProcessing,
                        "sLengthMenu":   window.MESSAGES.sLengthMenu,
                        "sZeroRecords":  window.MESSAGES.sZeroRecords,
                        "sInfo":         window.MESSAGES.sInfo,
                        "sInfoEmpty":    window.MESSAGES.sInfoEmpty,
                        "sSearch":       window.MESSAGES.search,
                     }
            });

            jQuery('#dob_gift_clients_table_wrapper .dataTables_filter input').addClass("form-control input-medium"); // modify table search input
            // modify table search input
            //jQuery('#sample_1_wrapper .dataTables_length select').addClass("form-control input-xsmall"); // modify table per page dropdown
            //jQuery('#sample_1_wrapper .dataTables_length select').select2(); // initialize select2 dropdown


        }

    };

}();