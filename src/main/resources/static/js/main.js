
function getCSRFToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

function getCSRFHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

function makeAjaxRequest(url, method, data, successCallback, errorCallback) {
    const xhr = new XMLHttpRequest();
    xhr.open(method, url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.setRequestHeader(getCSRFHeader(), getCSRFToken());
    
    xhr.onload = function() {
        if (xhr.status >= 200 && xhr.status < 300) {
            if (successCallback) {
                successCallback(JSON.parse(xhr.responseText));
            }
        } else {
            if (errorCallback) {
                errorCallback(xhr.status, xhr.responseText);
            } else {
                showAlert('Ошибка при выполнении запроса', 'danger');
            }
        }
    };
    
    xhr.onerror = function() {
        if (errorCallback) {
            errorCallback(0, 'Network error');
        } else {
            showAlert('Ошибка сети', 'danger');
        }
    };
    
    if (data) {
        xhr.send(JSON.stringify(data));
    } else {
        xhr.send();
    }
}

function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.container');
    if (container) {
        container.insertBefore(alertDiv, container.firstChild);

        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }
}

function convertCurrency(amount, fromCurrency, toCurrency) {
    makeAjaxRequest('/api/currency/convert', 'POST', {
        amount: amount,
        fromCurrency: fromCurrency,
        toCurrency: toCurrency
    }, function(response) {
        if (response.success) {
            document.getElementById('convertedAmount').textContent = 
                `${response.convertedAmount} ${toCurrency}`;
        } else {
            showAlert('Ошибка конвертации валюты', 'danger');
        }
    }, function(status, response) {
        showAlert('Ошибка при конвертации валюты', 'danger');
    });
}

function searchSpaces() {
    const location = document.getElementById('searchLocation').value;
    const name = document.getElementById('searchName').value;
    const minPrice = document.getElementById('minPrice').value;
    const maxPrice = document.getElementById('maxPrice').value;
    
    const params = new URLSearchParams();
    if (location) params.append('location', location);
    if (name) params.append('name', name);
    if (minPrice) params.append('minPrice', minPrice);
    if (maxPrice) params.append('maxPrice', maxPrice);
    
    makeAjaxRequest(`/api/spaces/search?${params.toString()}`, 'GET', null, 
        function(response) {
            if (response.success) {
                updateSpacesList(response.data);
            } else {
                showAlert('Ошибка при поиске', 'danger');
            }
        }, function(status, response) {
            showAlert('Ошибка при поиске пространств', 'danger');
        });
}

function updateSpacesList(spaces) {
    const container = document.getElementById('spacesContainer');
    if (!container) return;
    
    container.innerHTML = '';
    
    if (spaces.length === 0) {
        container.innerHTML = '<div class="col-12"><p class="text-center">Пространства не найдены</p></div>';
        return;
    }
    
    spaces.forEach(space => {
        const spaceCard = createSpaceCard(space);
        container.appendChild(spaceCard);
    });
}

function createSpaceCard(space) {
    const col = document.createElement('div');
    col.className = 'col-md-6 col-lg-4 mb-4';
    
    col.innerHTML = `
        <div class="card h-100">
            ${space.image ? `<img src="/uploads/images/${space.image}" class="card-img-top" alt="Пространство">` : ''}
            <div class="card-body">
                <h5 class="card-title">${space.name}</h5>
                <p class="card-text">${space.description}</p>
                <p class="card-text">
                    <strong>Местоположение:</strong> ${space.location}
                </p>
                <p class="card-text">
                    <strong>Цена:</strong> ${space.price} ₽/час
                </p>
                <div class="d-flex justify-content-between align-items-center">
                    <span class="badge ${space.availability ? 'bg-success' : 'bg-danger'}">
                        ${space.availability ? 'Доступно' : 'Занято'}
                    </span>
                    <a href="/spaces/${space.id}" class="btn btn-primary">Подробнее</a>
                </div>
            </div>
        </div>
    `;
    
    return col;
}

function createBooking(spaceId, startTime, endTime) {
    makeAjaxRequest('/api/bookings', 'POST', {
        spaceId: spaceId,
        startTime: startTime,
        endTime: endTime
    }, function(response) {
        if (response.success) {
            showAlert('Бронирование создано успешно!', 'success');
            setTimeout(() => {
                window.location.href = '/bookings';
            }, 2000);
        } else {
            showAlert(response.error || 'Ошибка при создании бронирования', 'danger');
        }
    }, function(status, response) {
        showAlert('Ошибка при создании бронирования', 'danger');
    });
}

function submitReview(spaceId, rating, comment) {
    makeAjaxRequest('/api/reviews', 'POST', {
        spaceId: spaceId,
        rating: rating,
        comment: comment
    }, function(response) {
        if (response.success) {
            showAlert('Отзыв добавлен успешно!', 'success');
            loadReviews(spaceId);
        } else {
            showAlert(response.error || 'Ошибка при добавлении отзыва', 'danger');
        }
    }, function(status, response) {
        showAlert('Ошибка при добавлении отзыва', 'danger');
    });
}

function loadReviews(spaceId) {
    makeAjaxRequest(`/api/spaces/${spaceId}/reviews`, 'GET', null, 
        function(response) {
            if (response.success) {
                updateReviewsSection(response.data);
            }
        }, function(status, response) {
            showAlert('Ошибка при загрузке отзывов', 'danger');
        });
}

function updateReviewsSection(reviews) {
    const container = document.getElementById('reviewsContainer');
    if (!container) return;
    
    container.innerHTML = '';
    
    if (reviews.length === 0) {
        container.innerHTML = '<p>Отзывов пока нет</p>';
        return;
    }
    
    reviews.forEach(review => {
        const reviewElement = createReviewElement(review);
        container.appendChild(reviewElement);
    });
}

function createReviewElement(review) {
    const div = document.createElement('div');
    div.className = 'review-item border-bottom pb-3 mb-3';
    
    const stars = '⭐'.repeat(review.rating);
    
    div.innerHTML = `
        <div class="d-flex justify-content-between">
            <h6>${review.user.username}</h6>
            <small class="text-muted">${new Date(review.createdAt).toLocaleDateString()}</small>
        </div>
        <div class="rating mb-2">${stars}</div>
        <p>${review.comment || ''}</p>
    `;
    
    return div;
}

function setupFileUpload() {
    const fileInput = document.getElementById('fileInput');
    const uploadArea = document.getElementById('uploadArea');
    
    if (!fileInput || !uploadArea) return;
    
    uploadArea.addEventListener('click', () => fileInput.click());
    
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });
    
    uploadArea.addEventListener('dragleave', () => {
        uploadArea.classList.remove('dragover');
    });
    
    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            fileInput.files = files;
            handleFileUpload(files[0]);
        }
    });
    
    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            handleFileUpload(e.target.files[0]);
        }
    });
}

function handleFileUpload(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/upload', true);
    xhr.setRequestHeader(getCSRFHeader(), getCSRFToken());
    
    xhr.onload = function() {
        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.success) {
                showAlert('Файл загружен успешно!', 'success');
                document.getElementById('uploadedFileName').textContent = file.name;
            } else {
                showAlert('Ошибка при загрузке файла', 'danger');
            }
        } else {
            showAlert('Ошибка при загрузке файла', 'danger');
        }
    };
    
    xhr.onerror = function() {
        showAlert('Ошибка сети при загрузке файла', 'danger');
    };
    
    xhr.send(formData);
}

function showLoading() {
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    overlay.innerHTML = '<div class="loading-spinner"></div>';
    document.body.appendChild(overlay);
}

function hideLoading() {
    const overlay = document.querySelector('.loading-overlay');
    if (overlay) {
        overlay.remove();
    }
}

document.addEventListener('DOMContentLoaded', function() {
    setupFileUpload();

    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            searchSpaces();
        });
    }

    const currencyForm = document.getElementById('currencyForm');
    if (currencyForm) {
        currencyForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const amount = document.getElementById('amount').value;
            const fromCurrency = document.getElementById('fromCurrency').value;
            const toCurrency = document.getElementById('toCurrency').value;
            convertCurrency(amount, fromCurrency, toCurrency);
        });
    }

    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert.parentNode) {
                alert.remove();
            }
        }, 5000);
    });
});

window.CoworkingBooking = {
    makeAjaxRequest,
    showAlert,
    convertCurrency,
    searchSpaces,
    createBooking,
    submitReview,
    loadReviews,
    setupFileUpload,
    showLoading,
    hideLoading
}; 