$(function() {
  var addDiv = $('#addinput');
  var i = $('#addinput p').size() + 1;
  $('#addNew').on('click', function() {
    $('<p><input class="form-control" type="text" name="p_new_' + i
      +'"  /><input type="hidden" value="' + i + '" name="lastField" /> </p>').appendTo(addDiv);
    //alert(i);
    i++;
    return false;
  });
  $('#remNew').on('click', function() {
    if( i > 2 ) {
      $(this).parents('p').remove();
      i--;
    }
    return false;
  });
});