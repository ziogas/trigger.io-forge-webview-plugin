trigger.io-forge-webview-plugin
===============================

Trigger.io forge easy to customize webview plugin

The main purpose of this plugin is to make "inline" webview look on top of other webview.
Also very usefull in html5 apps when you must use &lt;iframe&gt; but that &lt;iframe&gt; doesn't work as it should.

Just call this from javascript:

```
forge.internal.call(
    'webview.show',
    {url: 'http://github.com', padding_top: 0, padding_bottom: 0 },
    function () {},
    function (e) { alert('Error: '+e.message)}
);
```

or use provided api

```
forge.webview.show ( 'http://github.com', 0, 0 );
```

More information can be found at http://docs.trigger.io/en/v1.4/modules/native/index.html
