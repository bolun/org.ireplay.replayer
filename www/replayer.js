var Replayer = function () { };

Replayer.prototype.open = function (data, success, error) {
    cordova.exec(success, error, "Replayer", "open", [data]);
};

module.exports = new Replayer;