$(function () {
  var page = 0, query = '';
  var modal = new bootstrap.Modal(document.getElementById('applyModal'));

  function loadJobs() {
    Api.get('/api/public/jobs?q=' + encodeURIComponent(query) + '&page=' + page + '&size=6')
      .done(function (res) {
        var $list = $('#jobs').empty();
        $('#empty').toggleClass('d-none', res.content.length > 0);
        res.content.forEach(function (j) {
          var desc = j.description ? Api.esc(j.description.substring(0, 140)) + (j.description.length > 140 ? '...' : '') : '';
          $list.append(
            '<div class="col-md-6 col-lg-4"><div class="card job-card h-100"><div class="card-body d-flex flex-column">' +
            '<h5 class="card-title text-brand">' + Api.esc(j.title) + '</h5>' +
            '<h6 class="text-muted">' + Api.esc(j.company) + '</h6>' +
            '<div class="mb-2">' +
              (j.location ? '<span class="badge badge-soft me-1">' + Api.esc(j.location) + '</span>' : '') +
              (j.jobType ? '<span class="badge badge-soft me-1">' + Api.esc(j.jobType) + '</span>' : '') +
              (j.salary ? '<span class="badge badge-soft">' + Api.esc(j.salary) + '</span>' : '') +
            '</div>' +
            '<p class="card-text small flex-grow-1">' + desc + '</p>' +
            '<button class="btn btn-brand btn-sm apply-btn" data-id="' + j.id + '" data-title="' + Api.esc(j.title) + '">Apply now</button>' +
            '</div></div></div>');
        });
        renderPager(res);
      })
      .fail(function (xhr) { alert(Api.errorMessage(xhr)); });
  }

  function renderPager(res) {
    var $p = $('#pager').empty();
    if (res.totalPages <= 1) { return; }
    for (var i = 0; i < res.totalPages; i++) {
      $p.append('<li class="page-item' + (i === res.page ? ' active' : '') + '"><a class="page-link" href="#" data-p="' + i + '">' + (i + 1) + '</a></li>');
    }
  }

  $('#pager').on('click', 'a', function (e) { e.preventDefault(); page = +$(this).data('p'); loadJobs(); });
  $('#searchForm').on('submit', function (e) { e.preventDefault(); query = $('#q').val().trim(); page = 0; loadJobs(); });

  $('#jobs').on('click', '.apply-btn', function () {
    $('#applyForm')[0].reset();
    $('#applyAlert').addClass('d-none');
    $('#applyJobId').val($(this).data('id'));
    $('#applyTitle').text($(this).data('title'));
    modal.show();
  });

  $('#applyForm').on('submit', function (e) {
    e.preventDefault();
    var id = $('#applyJobId').val();
    Api.post('/api/public/jobs/' + id + '/apply', {
      candidateName: $('#candidateName').val(),
      email: $('#email').val(),
      phone: $('#phone').val(),
      coverLetter: $('#coverLetter').val()
    }).done(function (res) {
      $('#applyAlert').removeClass('d-none alert-danger').addClass('alert-success').text(res.message);
      setTimeout(function () { modal.hide(); }, 1200);
    }).fail(function (xhr) {
      $('#applyAlert').removeClass('d-none alert-success').addClass('alert-danger').text(Api.errorMessage(xhr));
    });
  });

  loadJobs();
});
