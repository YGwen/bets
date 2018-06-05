$(document).ready(function() {
  retrieveTeamDetails();
  removeAlreadyAssignedShirtNumbers();
});

function retrieveTeamDetails() {
  $("#teamDetails").load('/team/join/' + $('#availableTeams').val());
}

function removeAlreadyAssignedShirtNumbers() {
  $('#shirtNumber option').removeAttr('disabled');
  var request = $.ajax('/team/assignedShirts/' + $('#availableTeams').val());
  request.done(function (data) {
    data.forEach(function(v) {
      $('#shirtNumber option[value=' + v + ']').attr('disabled', 'disabled');
    });

    // Select first available shirt number
    $('#shirtNumber').val($('#shirtNumber option:not([disabled])').first().val());
  });


}