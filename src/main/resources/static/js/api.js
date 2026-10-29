/* Small AJAX helper built on jQuery.ajax. All calls return jqXHR promises. */
var Api = (function ($) {
  var TOKEN_KEY = 'jp_admin_token';

  function request(method, url, data, auth) {
    var opts = { url: url, method: method, contentType: 'application/json', dataType: 'json' };
    if (data !== undefined && data !== null) { opts.data = JSON.stringify(data); }
    if (auth) {
      var t = sessionStorage.getItem(TOKEN_KEY);
      if (t) { opts.headers = { 'Authorization': 'Bearer ' + t }; }
    }
    // 204 / empty bodies would fail JSON parsing, so accept any text and parse manually
    opts.dataType = 'text';
    opts.dataFilter = function (txt) { return txt; };
    return $.ajax(opts).then(function (txt) { return txt ? JSON.parse(txt) : null; });
  }

  function errorMessage(xhr) {
    try {
      var r = JSON.parse(xhr.responseText);
      if (r.details) { return Object.values(r.details).join(', '); }
      if (r.message) { return r.message; }
    } catch (e) { /* ignore */ }
    if (xhr.status === 0) { return 'Cannot reach server'; }
    return 'Request failed (' + xhr.status + ')';
  }

  function esc(s) {
    return $('<div>').text(s == null ? '' : s).html();
  }

  return {
    get: function (u, auth) { return request('GET', u, null, auth); },
    post: function (u, d, auth) { return request('POST', u, d, auth); },
    put: function (u, d, auth) { return request('PUT', u, d, auth); },
    del: function (u, auth) { return request('DELETE', u, null, auth); },
    setToken: function (t) { sessionStorage.setItem(TOKEN_KEY, t); },
    getToken: function () { return sessionStorage.getItem(TOKEN_KEY); },
    clearToken: function () { sessionStorage.removeItem(TOKEN_KEY); },
    errorMessage: errorMessage,
    esc: esc
  };
})(jQuery);
