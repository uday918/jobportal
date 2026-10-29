$(function () {
  var jobModal = new bootstrap.Modal(document.getElementById('jobModal'));
  var jobPage = 0, jobQuery = '', appPage = 0;

  // ---------- auth ----------
  function showLogin() { $('#appView').addClass('d-none'); $('#loginView').removeClass('d-none'); }
  function showApp() {
    $('#loginView').addClass('d-none'); $('#appView').removeClass('d-none');
    $('#whoami').text(sessionStorage.getItem('jp_admin_user') || '');
    loadDashboard();
  }
  function handleFail(xhr) {
    if (xhr.status === 401 || xhr.status === 403) { Api.clearToken(); showLogin(); return; }
    alert(Api.errorMessage(xhr));
  }

  $('#loginForm').on('submit', function (e) {
    e.preventDefault();
    Api.post('/api/auth/login', { username: $('#username').val(), password: $('#password').val() })
      .done(function (res) {
        Api.setToken(res.token);
        sessionStorage.setItem('jp_admin_user', res.username);
        $('#loginError').addClass('d-none'); $('#password').val('');
        showApp();
      })
      .fail(function (xhr) { $('#loginError').removeClass('d-none').text(Api.errorMessage(xhr)); });
  });
  $('#logoutBtn').on('click', function () { Api.clearToken(); showLogin(); });

  // ---------- tabs ----------
  $('.admin-nav .nav-link').on('click', function (e) {
    e.preventDefault();
    var tab = $(this).data('tab');
    $('.admin-nav .nav-link').removeClass('active'); $(this).addClass('active');
    $('section[id^=tab-]').addClass('d-none'); $('#tab-' + tab).removeClass('d-none');
    if (tab === 'dashboard') loadDashboard();
    if (tab === 'jobs') loadJobs();
    if (tab === 'applications') loadApps();
  });

  function pager($el, res, cb) {
    $el.empty();
    if (res.totalPages <= 1) return;
    for (var i = 0; i < res.totalPages; i++) {
      $el.append('<li class="page-item' + (i === res.page ? ' active' : '') + '"><a class="page-link" href="#" data-p="' + i + '">' + (i + 1) + '</a></li>');
    }
    $el.off('click').on('click', 'a', function (e) { e.preventDefault(); cb(+$(this).data('p')); });
  }

  // ---------- dashboard ----------
  function loadDashboard() {
    Api.get('/api/admin/dashboard', true).done(function (d) {
      var cards = [['Total jobs', d.totalJobs], ['Open jobs', d.openJobs], ['Applications', d.totalApplications], ['New applications', d.newApplications]];
      $('#stats').html(cards.map(function (c) {
        return '<div class="col-6 col-lg-3"><div class="card stat-card"><div class="card-body"><div class="text-muted">' + c[0] + '</div><div class="num">' + c[1] + '</div></div></div></div>';
      }).join(''));
      $('#topJobs').html(d.topJobs.map(function (t) {
        return '<li class="list-group-item d-flex justify-content-between">' + Api.esc(t.title) + '<span class="badge bg-primary rounded-pill">' + t.applications + '</span></li>';
      }).join('') || '<li class="list-group-item text-muted">No data</li>');
    }).fail(handleFail);
  }

  // ---------- jobs CRUD ----------
  function loadJobs() {
    Api.get('/api/admin/jobs?q=' + encodeURIComponent(jobQuery) + '&page=' + jobPage + '&size=8', true).done(function (res) {
      $('#jobsBody').html(res.content.map(function (j) {
        return '<tr><td>' + j.id + '</td><td>' + Api.esc(j.title) + '</td><td>' + Api.esc(j.company) + '</td><td>' + Api.esc(j.location) +
          '</td><td>' + Api.esc(j.jobType) + '</td><td><span class="badge ' + (j.status === 'OPEN' ? 'bg-success' : 'bg-secondary') + '">' + j.status + '</span></td>' +
          '<td class="text-end"><button class="btn btn-sm btn-outline-primary edit-job" data-id="' + j.id + '">Edit</button> ' +
          '<button class="btn btn-sm btn-outline-danger del-job" data-id="' + j.id + '">Delete</button></td></tr>';
      }).join('') || '<tr><td colspan="7" class="text-center text-muted">No jobs</td></tr>');
      pager($('#jobsPager'), res, function (p) { jobPage = p; loadJobs(); });
    }).fail(handleFail);
  }

  $('#jobSearch').on('submit', function (e) { e.preventDefault(); jobQuery = $('#jobQ').val().trim(); jobPage = 0; loadJobs(); });

  $('#addJobBtn').on('click', function () {
    $('#jobForm')[0].reset(); $('#jobId').val(''); $('#jobError').addClass('d-none');
    $('#jobModalTitle').text('Add Job'); jobModal.show();
  });

  $('#jobsBody').on('click', '.edit-job', function () {
    Api.get('/api/admin/jobs/' + $(this).data('id'), true).done(function (j) {
      $('#jobId').val(j.id); $('#jTitle').val(j.title); $('#jCompany').val(j.company); $('#jLocation').val(j.location);
      $('#jType').val(j.jobType || 'Full-time'); $('#jSalary').val(j.salary); $('#jStatus').val(j.status); $('#jDesc').val(j.description);
      $('#jobError').addClass('d-none'); $('#jobModalTitle').text('Edit Job'); jobModal.show();
    }).fail(handleFail);
  });

  $('#jobsBody').on('click', '.del-job', function () {
    if (!confirm('Delete this job and all its applications?')) return;
    Api.del('/api/admin/jobs/' + $(this).data('id'), true).done(loadJobs).fail(handleFail);
  });

  $('#jobForm').on('submit', function (e) {
    e.preventDefault();
    var id = $('#jobId').val();
    var body = { title: $('#jTitle').val(), company: $('#jCompany').val(), location: $('#jLocation').val(), jobType: $('#jType').val(),
                 salary: $('#jSalary').val(), description: $('#jDesc').val(), status: $('#jStatus').val() };
    var req = id ? Api.put('/api/admin/jobs/' + id, body, true) : Api.post('/api/admin/jobs', body, true);
    req.done(function () { jobModal.hide(); loadJobs(); })
       .fail(function (xhr) {
         if (xhr.status === 401 || xhr.status === 403) { jobModal.hide(); handleFail(xhr); return; }
         $('#jobError').removeClass('d-none').text(Api.errorMessage(xhr));
       });
  });

  // ---------- applications ----------
  function loadApps() {
    Api.get('/api/admin/applications?page=' + appPage + '&size=8', true).done(function (res) {
      $('#appsBody').html(res.content.map(function (a) {
        var opts = ['NEW', 'SHORTLISTED', 'REJECTED'].map(function (s) {
          return '<option' + (s === a.status ? ' selected' : '') + '>' + s + '</option>';
        }).join('');
        return '<tr><td>' + a.id + '</td><td>' + Api.esc(a.jobTitle) + '</td><td>' + Api.esc(a.candidateName) + '</td><td>' + Api.esc(a.email) +
          '</td><td>' + Api.esc(a.phone) + '</td><td class="cover">' + Api.esc(a.coverLetter) + '</td>' +
          '<td><select class="form-select form-select-sm app-status" data-id="' + a.id + '">' + opts + '</select></td>' +
          '<td>' + Api.esc((a.appliedAt || '').replace('T', ' ').substring(0, 16)) + '</td>' +
          '<td><button class="btn btn-sm btn-outline-danger del-app" data-id="' + a.id + '">Delete</button></td></tr>';
      }).join('') || '<tr><td colspan="9" class="text-center text-muted">No applications yet</td></tr>');
      pager($('#appsPager'), res, function (p) { appPage = p; loadApps(); });
    }).fail(handleFail);
  }

  $('#appsBody').on('change', '.app-status', function () {
    Api.put('/api/admin/applications/' + $(this).data('id') + '/status', { status: $(this).val() }, true).fail(handleFail);
  });
  $('#appsBody').on('click', '.del-app', function () {
    if (!confirm('Delete this application?')) return;
    Api.del('/api/admin/applications/' + $(this).data('id'), true).done(loadApps).fail(handleFail);
  });

  // ---------- init ----------
  if (Api.getToken()) { showApp(); } else { showLogin(); }
});
