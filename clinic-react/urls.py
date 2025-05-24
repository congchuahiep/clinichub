from django.urls import path
from .views import CreateMomoPaymentView, ConfirmMomoPaymentView

urlpatterns = [
    path('create-payment/', CreateMomoPaymentView.as_view(), name='create_momo_payment'),
    # dont add slash at the end url, for some reason momo will not callback
    path('confirm-payment/<int:order_id>', ConfirmMomoPaymentView.as_view(), name='confirm_momo_payment'),
]