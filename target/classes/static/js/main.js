/* Apex Online Banking System - Interactive UI Scripts */

document.addEventListener('DOMContentLoaded', function() {

    // 1. Auto-dismiss Alert Messages after 6 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 6000);
    });

    // 2. Transfer Confirmation Modal Interceptor
    const transferForm = document.getElementById('transferForm');
    if (transferForm) {
        transferForm.addEventListener('submit', function(e) {
            const confirmBtn = document.getElementById('confirmTransferBtn');
            if (confirmBtn && !transferForm.dataset.confirmed) {
                e.preventDefault();

                const recipientAcc = document.getElementById('recipientAccountNumber').value;
                const amount = document.getElementById('amount').value;

                document.getElementById('modalRecipientAcc').innerText = recipientAcc;
                document.getElementById('modalTransferAmount').innerText = '$' + parseFloat(amount).toFixed(2);

                const modalElement = new bootstrap.Modal(document.getElementById('transferConfirmModal'));
                modalElement.show();
            }
        });

        const executeBtn = document.getElementById('executeTransferModalBtn');
        if (executeBtn) {
            executeBtn.addEventListener('click', function() {
                transferForm.dataset.confirmed = "true";
                transferForm.submit();
            });
        }
    }

    // 3. Password Match Live Feedback
    const passInput = document.getElementById('password');
    const confirmInput = document.getElementById('confirmPassword');
    const matchFeedback = document.getElementById('passwordMatchMsg');

    if (passInput && confirmInput && matchFeedback) {
        function checkPasswords() {
            if (confirmInput.value.length === 0) {
                matchFeedback.innerText = '';
                return;
            }
            if (passInput.value === confirmInput.value) {
                matchFeedback.innerText = '✓ Passwords match';
                matchFeedback.className = 'form-text text-success font-weight-bold';
            } else {
                matchFeedback.innerText = '✗ Passwords do not match';
                matchFeedback.className = 'form-text text-danger font-weight-bold';
            }
        }
        passInput.addEventListener('input', checkPasswords);
        confirmInput.addEventListener('input', checkPasswords);
    }
});
