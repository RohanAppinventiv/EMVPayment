package com.quivioedge.emvpayment.pos

import java.util.UUID

class DsiEMVRequestBuilder() {

    private val merchantID = "SONNYTAMA35000GP"
    private val pinPadIpAddress = "127.0.0.1"
    private val pinPadIpPort = "1235"
    private val operationMode = "CERT"
    private var sequenceNo = 10
    private val secureDevice = "EMV_A920PRO_DATACAP_E2E"
    private val posPackageID = "dsiEMVAndroid:1.0"

    private fun getUserTrace() = UUID.randomUUID()

    fun nextSequenceNo(): String {
        sequenceNo++
        return "001001%04d".format(sequenceNo)
    }

    fun buildCollectCardDataResponse(): String {
        return """
            <?xml version="1.0" ?>
                <TStream>
                  <SecureDevice>${secureDevice}</SecureDevice>
                  <SequenceNo>${nextSequenceNo()}</SequenceNo>
                  <UserTrace>${getUserTrace()}</UserTrace>
                  <PlaceHolderAmount>1.00</PlaceHolderAmount>
                  <MerchantID>${merchantID}</MerchantID>
                  <PinPadIpAddress>${pinPadIpAddress}</PinPadIpAddress>
                  <PinPadIpPort>${pinPadIpPort}</PinPadIpPort>
                  <POSPackageID>${posPackageID}</POSPackageID>
                  <TranCode>${TransType.EMVSale.name}</TranCode>
            </TStream>
        """.trimIndent()
    }
    fun buildPrintReceiptRequest(): String {
        return """
            <?xml version="1.0" ?>
            <TStream>
                <Transaction>
                    <TranCode>${TransType.PrintReceipt.name}</TranCode>
                    <OperationMode>${operationMode}</OperationMode>
                    <MerchantID>${merchantID}</MerchantID>
                    <TerminalID>00000001</TerminalID>
                    <POSPackageID>${posPackageID}</POSPackageID>
                    <SecureDevice>$secureDevice</SecureDevice>
                    <PinPadIpAddress>$pinPadIpAddress</PinPadIpAddress>
                    <PinPadIpPort>$pinPadIpPort</PinPadIpPort>
                    <OperatorID>01</OperatorID>
                    <UserTrace>${getUserTrace()}</UserTrace>
                    <SequenceNo>${nextSequenceNo()}</SequenceNo>
                    <Line1>.</Line1>
                    <Line2>.                  SALE                  </Line2>
                    <Line3>.</Line3>
                    <Line4>.MASTERCARD             ************0060</Line4>
                    <Line5>.ENTRY METHOD: CHIP</Line5>
                    <Line6>.DATE: 11/22/2022  TIME: 17:06:50</Line6>
                    <Line7>.</Line7>
                    <Line8>.INVOICE: 1772039603</Line8>
                    <Line9>.REFERENCE: 2707683596949379</Line9>
                    <Line10>.AUTH CODE: 30158P</Line10>
                    <Line11>.</Line11>
                    <Line12>.AMOUNT                      USD${'$'} 23.16</Line12>
                    <Line13>.                            ==========</Line13>
                    <Line14>.TOTAL                       USD${'$'} 23.16</Line14>
                    <Line15>.</Line15>
                    <Line16>.          APPROVED - THANK YOU          </Line16>
                    <Line17>.</Line17>
                    <Line18>.I AGREE TO PAY THE ABOVE TOTAL AMOUNT</Line18>
                    <Line19>.ACCORDING TO CARD ISSUER AGREEMENT</Line19>
                    <Line20>.(MERCHANT AGREEMENT IF CREDIT VOUCHER)</Line20>
                    <Line21>.</Line21>
                    <Line22>.</Line22>
                    <Line23>.</Line23>
                    <Line24>.x_______________________________________</Line24>
                    <Line25>.        Test Card 02      Datacap       </Line25>
                    <Line26>.</Line26>
                    <Line27>.</Line27>
                    <Line28>.APPLICATION LABEL: Mastercard      </Line28>
                    <Line29>.AID: A0000000041010</Line29>
                    <Line30>.TVR: 8000088000</Line30>
                    <Line31>.IAD: 0114A00009220000000000000000000000F</Line31>
                    <Line32>.TSI: 6800</Line32>
                </Transaction>
            </TStream>
        """.trimIndent()
    }
    fun buildEMVParamDownloadRequest(): String {
        return """
        <?xml version="1.0"?>
        <TStream>
        <Admin>
        <OperationMode>${operationMode}</OperationMode>    
        <PinPadIpAddress>${pinPadIpAddress}</PinPadIpAddress>
        <PinPadIpPort>${pinPadIpPort}</PinPadIpPort>
        <TranCode>${TransType.EMVParamDownload.name}</TranCode>
        <MerchantID>${merchantID}</MerchantID>
        <TerminalID>00000001</TerminalID>
        <SequenceNo>${nextSequenceNo()}</SequenceNo>
        <POSPackageID>${posPackageID}</POSPackageID>
        <SecureDevice>${secureDevice}</SecureDevice>
        <OperatorID>01</OperatorID>
        </Admin>
        </TStream>
    """.trimIndent()
    }

    fun buildPinPadResetRequest(): String {
        return """
      <?xml version="1.0"?>
      <TStream>
        <Transaction>
        <OperationMode>${operationMode}</OperationMode>    
        <PinPadIpAddress>${pinPadIpAddress}</PinPadIpAddress>
        <UseForms>Suppressed</UseForms> 
        <PinPadIpPort>${pinPadIpPort}</PinPadIpPort>          
        <MerchantID>${merchantID}</MerchantID>
        <POSPackageID>${posPackageID}</POSPackageID>
        <SecureDevice>${secureDevice}</SecureDevice>
        <SequenceNo>${nextSequenceNo()}</SequenceNo>
        <TranCode>${TransType.EMVPadReset.name}</TranCode>
        <OperatorID>01</OperatorID>
        </Transaction>
      </TStream>
    """.trimIndent()
    }

    fun buildEMVSaleRequest(): String {
        return """<?xml version="1.0"?>
        <TStream>
        <Transaction>
        <SequenceNo>${nextSequenceNo()}</SequenceNo>
        <UserTrace>${getUserTrace()}</UserTrace>
        <POSPackageID>${posPackageID}</POSPackageID>
        <OperatorID>01</OperatorID>
        <OperationMode>${operationMode}</OperationMode>    
        <PinPadIpAddress>${pinPadIpAddress}</PinPadIpAddress>
        <PinPadIpPort>${pinPadIpPort}</PinPadIpPort>
        <MerchantID>${merchantID}</MerchantID>
        <SecureDevice>${secureDevice}</SecureDevice>
        <InvoiceNo>1</InvoiceNo>
        <RefNo>1</RefNo>
        <Amount>
            <Purchase>1.00</Purchase>
            <Gratuity>0.00</Gratuity>
            <CashBack>0.00</CashBack>
        </Amount>
        <TranCode>${TransType.EMVSale.name}</TranCode>
        </Transaction>
        </TStream>""".trimIndent()
    }
}