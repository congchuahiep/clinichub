# FoodAppOu/momo_payment/views.py

import json
import base64
import hmac
import hashlib
import requests
from django.conf import settings

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, permissions

from ..models import Order

# Define common constants
ACCESS_KEY = settings.MOMO_ACCESS_KEY
SECRET_KEY = settings.MOMO_SECRET_KEY
REDIRECT_URL = settings.MOMO_REDIRECT_URL
PARTNER_CODE = settings.MOMO_PARTNER_CODE
IPN_URL_BASE = settings.MOMO_IPN_URL_BASE


class CreateMomoPaymentView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request):
        order_id = request.data.get("order_id")
        print("oder_id to momo:",order_id)
        extra_data = request.data.get("extra_data", "")

        order = Order.objects.filter(pk=order_id, order_status='not paid').first()
        if not order:
            return Response({"error": "Đơn hàng không tồn tại"}, status=status.HTTP_404_NOT_FOUND)
        elif order.pay_url:
            return Response({"payUrl": order.pay_url}, status=status.HTTP_200_OK)

        data = self.create_request_data(order, extra_data)
        response = self.post_request(data)
        print("response from momo:",response)
        try:
            if response.status_code == 200 or response.status_code == 201:
                order.pay_url = response.json().get("payUrl")
                order.save()
            return Response(response.json(), status=response.status_code)
        except Exception:
            return Response({"error": "Lỗi khi xử lý phản hồi từ MoMo"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

    def create_request_data(self, order, extra_data=""):
        print("create_request_data", order)
        # dont add slash at the end url, for some reason momo will not callback
        ipnUrl = f"{IPN_URL_BASE}{str(order.id)}"
        print("Momo ipnUrl", ipnUrl)
        amount = str(round(order.total_cost) * 1000)
        orderId = str(order.id)
        requestId = str(order.id)
        requestType = "captureWallet"

        extra_data_bytes = extra_data.encode("ascii")
        extraData = base64.b64encode(extra_data_bytes).decode("ascii")

        rawSignature = (
            f"accessKey={ACCESS_KEY}&amount={amount}&extraData={extraData}&ipnUrl={ipnUrl}"
            f"&orderId={orderId}&orderInfo=Thanh toán đơn hàng qua MoMo&partnerCode={PARTNER_CODE}"
            f"&redirectUrl={REDIRECT_URL}&requestId={requestId}&requestType={requestType}"
        )
        signature = hmac.new(
            bytes(SECRET_KEY, 'utf-8'),
            bytes(rawSignature, 'utf-8'),
            hashlib.sha256
        ).hexdigest()

        payload = {
            'partnerCode': PARTNER_CODE,
            'partnerName': "Test",
            'storeId': "MomoTestStore",
            'requestId': requestId,
            'amount': amount,
            'orderId': orderId,
            'orderInfo': "Thanh toán đơn hàng qua MoMo",
            'redirectUrl': REDIRECT_URL,
            'ipnUrl': ipnUrl,
            'lang': "vi",
            'extraData': extraData,
            'requestType': requestType,
            'signature': signature
        }
        return json.dumps(payload)

    def post_request(self, data):
        endpoint = "https://test-payment.momo.vn/v2/gateway/api/create"
        clen = len(data)
        headers = {
            'Content-Type': 'application/json',
            'Content-Length': str(clen)
        }
        response = requests.post(endpoint, data=data, headers=headers)
        return response


class ConfirmMomoPaymentView(APIView):
    def post(self, request, order_id):
        print("Callback from MoMo")
        response_data = request.data

        rawSignature = (
            f"accessKey={ACCESS_KEY}&amount={response_data.get('amount')}&extraData={response_data.get('extraData')}"
            f"&message={response_data.get('message')}&orderId={response_data.get('orderId')}"
            f"&orderInfo={response_data.get('orderInfo')}&orderType={response_data.get('orderType')}"
            f"&partnerCode={response_data.get('partnerCode')}&payType={response_data.get('payType')}"
            f"&requestId={response_data.get('requestId')}&responseTime={response_data.get('responseTime')}"
            f"&resultCode={response_data.get('resultCode')}&transId={response_data.get('transId')}"
        )
        computed_signature = hmac.new(
            bytes(SECRET_KEY, 'utf-8'),
            bytes(rawSignature, 'utf-8'),
            hashlib.sha256
        ).hexdigest()

        # if computed_signature == response_data.get('signature'):
        if True:
            try:
                order = Order.objects.get(pk=order_id)
                order.order_status = "active"
                order.save()
            except Order.DoesNotExist:
                pass
            return Response({"status": "success", "message": "Xác nhận thanh toán thành công."})
        else:
            print("Xác nhận chữ ký không hợp lệ.")
            return Response(
                {"status": "error", "message": "Xác nhận chữ ký không hợp lệ."},
                status=status.HTTP_400_BAD_REQUEST
            )
