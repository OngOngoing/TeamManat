$(function () {
    $('.tooltipped').tooltip({delay: 50});
});

var startColor = '#FC5B3F';
var endColor = '#29E68E';

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
            $('.progressbar-text').css('color',state.color).css('font-size', '5rem');
            bar.path.setAttribute('stroke',state.color);
            bar.path.setAttribute('stroke-width', state.width);
        }

    });

    circle.animate($('#progress-circle').attr('value'), {
        from: {color: startColor, width: 0.5},
        to: {color: endColor, width: 3}
    });
};
