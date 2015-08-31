
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
    var offer_relation = request.object.relation("offer");
    var offer_query = offer_relation.query().include("user").first({
        success: function(object) {
            var offer_id = object.get("user").id;
            var offer_email = object.get("user").get("email");
            var request_relation = request.object.relation("request");
            var request_query = offer_relation.query().include("user").first({
                success: function(object) {
                    var request_id = object.get("user").id;
                    var request_email = object.get("user").get("email");
                    var push_id = 0;
                    var push_email = "";
                    console.log("offer_id = " + offer_id + ". request_id = " + request_id + ". sender_id = " + sender_id);
                    if (offer_id === sender_id) {
                        push_id = request_id;
                        push_email = request_email;
                    } else {
                        push_id = offer_id;
                        push_email = offer_email;
                    }
                    var installation_query = new Parse.Query(Parse.Installation);
                    query.equalTo("user", push_id);
                    Parse.Push.send({
                        where: installation_query, // Set our Installation query
                        data: {
                            alert: "There is a possible match with " + push_email + ". Click to see."
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
    var query = new Parse.Query(Parse.User);
    query.get(request)
})