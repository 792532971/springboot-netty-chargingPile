package com.example.springbootnettyserver.pojo;

/**
 * @desc:
 * @author: Administrator
 * @date: 2024/3/15 0015 14:11
 */
public class BytesData {

    private static final byte START_BYTE_1 = (byte) 0xFA;
    private static final byte START_BYTE_2 = (byte) 0xF5;

    private byte version;
    private byte sequenceNumber;
    private byte commandCode;
    private byte[] data;
    private byte checksum;

    // 构造方法
    public BytesData(byte version, byte sequenceNumber, byte commandCode, byte[] data) {
        this.version = version;
        this.sequenceNumber = sequenceNumber;
        this.commandCode = commandCode;
        this.data = data;
        this.checksum = calculateChecksum();
    }

    // 计算校验和
    private byte calculateChecksum() {
        int sum = version + sequenceNumber + commandCode;
        for (byte b : data) {
            sum += b;
        }
        return (byte) sum;
    }

    // 生成协议报文的字节数组
    public byte[] toByteArray() {
        byte[] byteArray = new byte[4 + data.length]; // 4 是起始域、版本域、序列号域和命令代码的长度

        byteArray[0] = START_BYTE_1;
        byteArray[1] = START_BYTE_2;
        byteArray[2] = version;
        byteArray[3] = sequenceNumber;
        byteArray[4] = commandCode;
        System.arraycopy(data, 0, byteArray, 5, data.length); // 将数据域拷贝到字节数组中
        byteArray[byteArray.length - 1] = checksum;

        return byteArray;
    }

    // Getters 和 Setters 方法
    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(byte sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public byte getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(byte commandCode) {
        this.commandCode = commandCode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getChecksum() {
        return checksum;
    }

    public void setChecksum(byte checksum) {
        this.checksum = checksum;
    }
}
