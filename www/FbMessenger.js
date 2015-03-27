var exec = require('cordova/exec');

exports.shareMethod = function(arg0, success, error) {
    exec(success, error, "FbMessenger", "shareMethod", [arg0]);
};
