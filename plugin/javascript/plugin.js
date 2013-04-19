forge.webview = {

    show: function ( url, padding_top, padding_bottom, success, error ){

        success = success || function(){};
        error = error || function(){};

        forge.internal.call ( 'webview.show', { url: url, padding_top: parseInt ( padding_top, 10 ), padding_bottom: parseInt ( padding_bottom, 10 ) }, success, error )
    },
    back: function ( success, error ){

        success = success || function(){};
        error = error || function(){};

        forge.internal.call ( 'webview.goBack', {}, success, error );
    },
    close: function ( success, error ){

        success = success || function(){};
        error = error || function(){};

        forge.internal.call ( 'webview.close', {}, success, error );
    }
}
