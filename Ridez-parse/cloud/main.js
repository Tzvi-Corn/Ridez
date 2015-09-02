
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
    response.success("Hello world!");
});

Parse.Cloud.define("sendInvitationMail", function(request, response) {
    console.log("SendInvitationMail");
    console.log(request.params);
    var Mandrill = require('mandrill');
    Mandrill.initialize('nNWTv0cgRBcKJUN4T38P7Q');

    name = request.params.name;
    email = request.params.email;
    sender_name = request.params.sender_name;
    sender_mail = request.params.sender_mail;
    group = request.params.group;

    Mandrill.sendEmail({
        message: {
            text: "Hi " + name + ",\n" + sender_name + " (" + sender_mail + ") invites you to join his group: " + group + ".\nTo join, please download Ridez: http://example.com",
            subject: sender_name + " invites you to his Ridez Group",
            from_email: "do-not-reply@ridez.parseapp.com",
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

Parse.Cloud.afterSave("potentialMatch", function(request) {
    console.log(request);
    var sender_id = request.user.id;
    request.object.relation("offer").query().include("user").first({
        success: function(offer_ride) {
            var offer_user = offer_ride.get("user");
            var offer_user_id = offer_user.id;
            var offer_email = offer_user.get("email");
            request.object.relation("request").query().include("user").first({
                success: function(request_ride) {
                    var request_user = request_ride.get("user");
                    var request_user_id = request_user.id;
                    var request_email = request_user.get("email");
                    var push_user = null;
                    var push_id = 0;
                    var push_email = "";
                    if (offer_user_id === sender_id) {
                        push_ride = request_ride;
                        push_user = request_user;
                        push_user_id = request_user_id;
                        push_email = offer_email;
                    } else {
                        push_ride = offer_ride;
                        push_user = offer_user;
                        push_user_id = offer_user_id;
                        push_email = request_email;
                    }
                    console.log("offer_user_id = " + offer_user_id + ". request_user_id = " + request_user_id + ". sender_id = " + sender_id + ". push_user_id = " + push_user_id);
                    var installation_query = new Parse.Query(Parse.Installation);
                    installation_query.equalTo("user", push_user);
                    Parse.Push.send({
                        where: installation_query, // Set our Installation query
                        data: {
                            alert: "There is a possible match with " + push_email + ". Click to see.",
                            type: 1,
                            match_id: request.object.id,
                            ride_id: push_ride.id,
                        }
                    }, {
                        success: function() {
                            console.log("Success");
                        },
                        error: function(error) {
                            console.log("ERROR");
                        }
                    });
                }
            });           
        }
    });
})