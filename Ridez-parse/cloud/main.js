
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