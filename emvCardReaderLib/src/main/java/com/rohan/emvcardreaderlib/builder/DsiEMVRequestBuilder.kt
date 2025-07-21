package com.rohan.emvcardreaderlib.builder

import com.rohan.emvcardreaderlib.Amount
import com.rohan.emvcardreaderlib.TransType
import java.util.UUID

class DsiEMVRequestBuilder() {

    private val merchantID = "SONNYTAMA35000GP"
    private val operationMode = "CERT"
    private var sequenceNo = 10
    private val secureDevice = "EMV_VP3350_DATACAP"
    private val bluetoothDevice = "IDTECH-VP3350-11311"
    private val posPackageID = "dsiEMVAndroid:1.0"

    private fun getUserTrace() = UUID.randomUUID()

    fun nextSequenceNo(): String {
        sequenceNo++
        return "001001%04d".format(sequenceNo)
    }

    fun buildPinPadResetRequest(): String {
        return """
            <?xml version="1.0"?>
            <TStream>
            <Transaction>
            <OperationMode>${operationMode}</OperationMode>    
            <UseForms>Suppressed</UseForms> 
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

    fun buildCollectCardDataRequest(): String {
        return """<?xml version="1.0" ?>
              <TStream>
                <Transaction>
                <ComPort>1</ComPort>
                <BluetoothDeviceName>${bluetoothDevice}</BluetoothDeviceName>
                <SecureDevice>${secureDevice}</SecureDevice>
                <SequenceNo>${nextSequenceNo()}</SequenceNo>
                <UserTrace>${getUserTrace()}</UserTrace>
                <MerchantID>${merchantID}</MerchantID>
                <TranCode>${TransType.GetPrePaidStripe.name}</TranCode>
                <Account>
                <AcctNo>SecureDevice</AcctNo>
                </Account>
                <MinLen>0</MinLen>
                <MaxLen>19</MaxLen>
                </Transaction>
            </TStream>
        """.trimIndent()
    }

    fun buildEMVSaleRequest(amount: String): String {
        return """<?xml version="1.0"?>
        <TStream>
        <Transaction>
        <SequenceNo>${nextSequenceNo()}</SequenceNo>
        <UserTrace>${getUserTrace()}</UserTrace>
        <POSPackageID>${posPackageID}</POSPackageID>
        <OperatorID>01</OperatorID>
        <OperationMode>${operationMode}</OperationMode>    
        <MerchantID>${merchantID}</MerchantID>
        <SecureDevice>${secureDevice}</SecureDevice>
        <InvoiceNo>1</InvoiceNo>
        <RefNo>1</RefNo>
        <Amount>
            <Purchase>${amount}</Purchase>
            <Gratuity>0.00</Gratuity>
            <CashBack>0.00</CashBack>
        </Amount>
        <TranCode>${TransType.EMVSale.name}</TranCode>
        </Transaction>
        </TStream>""".trimIndent()
    }

    fun buildEMVParamDownloadRequest(): String {
        return """
        <?xml version="1.0"?>
        <TStream>
        <Admin>
        <OperationMode>${operationMode}</OperationMode>    
        <TranCode>${TransType.EMVParamDownload.name}</TranCode>
        <MerchantID>${merchantID}</MerchantID>
        <SequenceNo>${nextSequenceNo()}</SequenceNo>
        <POSPackageID>${posPackageID}</POSPackageID>
        <SecureDevice>${secureDevice}</SecureDevice>
        <OperatorID>01</OperatorID>
        </Admin>
        </TStream>
    """.trimIndent()
    }
}