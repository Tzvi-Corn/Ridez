// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});
 
Parse.Cloud.define("sendInvitationMail", function(request, response) {
var Mandrill = require('mandrill');
Mandrill.initialize('nNWTv0cgRBcKJUN4T38P7Q');
 
name = request.params.name;
email = request.params.email;
sender_name = request.params.sender_name;
group = request.params.group;
 
Mandrill.sendEmail({
message: {
text: "Hi " + name + ",\n" + sender_name + " invites you to join his group: " + group + ".\nTo join, please download Ridez: http://example.com",
subject: sender_name + " invites you to his Ridez Group",
from_email: "simadin@gmail.com",
from_name: "Ridez app",
to: [
{
email: email,
name: name
}
]
},
async: true
},{
success: function(httpResponse) {
console.log(httpResponse);
response.success("Email sent!");
},
error: function(httpResponse) {
console.error(httpResponse);
response.error("Uh oh, something went wrong");
}
});
});

Parse.Cloud.afterSave("Ride", function(request) {
  query = new Parse.Query("Ride");
  query.include("from");
  query.include("to");
  query.notEqualTo("objectId", request.object.get("objectId"));
  var myFrom = request.object.get("from").get("point");
  var myTo = request.object.get("to").get("point");
  Parse.Cloud.httpRequest({
                method: "GET",
                url: 'https://maps.googleapis.com/maps/api/directions/json',
                params: {
                    origin:myFrom.latitude + "," + myFrom.longitude,
					destination:myTo.latitude + "," + myTo.longitude,
                    key:"AIzaSyDQljX1PO_KSvCaunIkp0XptCkitMVBE_U"
                }).then(function(httpResponse) {
				  console.log(httpResponse.text);
				  var response=httpResponse.data;
                    if(response.status == "OK"){
                        var minutes = response.routes[0].legs[0].duration.value / 60;
                     }
				}, function(httpResponse) {
				  console.error('Request failed with response code ' + httpResponse.status);
				});
               
            });
  query.find({
  success: function(results) {
    for (var i = 0; i < results.length; i++) {
      var object = results[i];
	  var possibleFrom = object.get("from").get("point");
	  var possibleTo = object.get("to").get("point");
      Parse.Cloud.httpRequest({
                method: "GET",
                url: 'https://maps.googleapis.com/maps/api/directions/json',
                params: {
                    origin:possibleFrom.latitude + "," + possibleFrom.longitude,
					destination:possibleTo.latitude + "," + possibleTo.longitude,
					waypoints: myFrom.latitude + "," + myfrom.longitude + "|" myTo.latitude + "," + myTo.longitude,
                    key:"AIzaSyDQljX1PO_KSvCaunIkp0XptCkitMVBE_U"
                }).then(function(httpResponse) {
				  console.log(httpResponse.text);
				  if(httpResponse.status == "OK"){
						var response=httpResponse.data;
                    
						var legs = response.routes[0].legs;
						var minutes2 = 0;
						for (var j = 0; j < legs.length; j++) {
							minutes2 += legs[j].duration.value / 60;
						}
						if (minutes2 < minutes + 10) {
							var PotentialMatch = Parse.Object("potentialMatch");
							var offerRelation = PotentialMatch.relation("offer");
							var requestRelation = PotentialMatch.relation("relation");
							potentialMatch.set("isConfirmed", false);
							offerRelation.add(request.object.get("isRequest")? object:request.object);
							offerRelation.add(request.object.get("isRequest")? request.object:object);


							PotentialMatch.save();
                        
						
						}
				}, function(httpResponse) {
				  console.error('Request failed with response code ' + httpResponse.status);
				});
               
            });
    }
  },
  error: function(error) {
    alert("Error: " + error.code + " " + error.message);
  }
});
});