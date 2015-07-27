$(function () {
    $('.tooltipped').tooltip({delay: 50});
    $('.dropdown-button').dropdown({
            inDuration: 0,
            belowOrigin: true,
            constrain_width: true
    });
});



var startColor = '#FC5B3F';
var endColor = '#6FD57F';

window.onload = function onLoad() {
    var circle = new ProgressBar.Circle('#progress-circle', {
        strokeWidth: 3,
        trailWidth: 1,
        text: {
            value: '0'
        },
        color: startColor,
        duration: 1500,
        easing: 'easeInOut',
        step: function(state, bar) {
            bar.setText((bar.value() * 100).toFixed(0) + '%');
            $('.progressbar-text').css('color',state.color);
            bar.path.setAttribute('stroke',state.color);
            bar.path.setAttribute('stroke-width', state.width);
        }

    });

    circle.animate($('#progress-circle').attr('value'), {
        from: {color: startColor, width: 0.5},
        to: {color: endColor, width: 3}
    });
};
