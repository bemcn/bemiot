package org.bem.iot.util;

import org.bem.iot.entity.system.*;
import oshi.SystemInfo;
import oshi.hardware.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SystemInfoUtil {
    /**
     * 获取CPU信息
     * <br> 建议每秒或500毫秒轮询
     * @return CPU信息
     */
    public static Cpu getCpuInfo(SystemInfo systemInfo) {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        CentralProcessor.ProcessorIdentifier identifier = processor.getProcessorIdentifier();

        String vendor = identifier.getVendor();
        vendor = vendor.replace("Genuine", "");
        String vendorFreq = ConvertUtil.formatVendorFreq(identifier.getVendorFreq());

        Cpu cpu = new Cpu();
        cpu.setProcessorID(identifier.getProcessorID());
        cpu.setCpuName(identifier.getName());
        cpu.setCpuPhysicalNumber(processor.getPhysicalProcessorCount());
        cpu.setCpuLogicalNumber(processor.getLogicalProcessorCount());
        cpu.setVendor(vendor);
        cpu.setVendorFreq(vendorFreq);
        if(identifier.isCpu64bit()) {
            cpu.setCpuBit(64);
        } else {
            cpu.setCpuBit(32);
        }
        cpu.setMicroarchitecture(identifier.getMicroarchitecture());
        return cpu;
    }

    /**
     * 实时CPU利用率
     * @return 利用率（百分比）
     */
    public static long cpuUtilizationRate(SystemInfo systemInfo) {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        // 获取cpu利用率
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(500); // 等待1秒获取准确数据
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String cpuUtilRateStr = df.format(processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100d);
        return Long.parseLong(cpuUtilRateStr);
    }

    /**
     * 获取内存信息
    */
    public static Memory getMemoryInfo(SystemInfo systemInfo) {
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        List<PhysicalMemory> physicalMemoryList = globalMemory.getPhysicalMemory();

        Memory memory = new Memory();
        List<MemoryItem> memoryItems = new ArrayList<>();

        for (PhysicalMemory physicalMemory : physicalMemoryList) {
            MemoryItem memoryItem = new MemoryItem();
            memoryItem.setBankLabel(physicalMemory.getBankLabel());
            memoryItem.setManufacturer(physicalMemory.getManufacturer());
            memoryItem.setMemoryType(physicalMemory.getMemoryType());
            memoryItem.setCapacity(ConvertUtil.formatCapacity(physicalMemory.getCapacity(), true, 0));
            memoryItem.setClockSpeed(ConvertUtil.formatCapacity(physicalMemory.getClockSpeed(), true, 0));
            memoryItems.add(memoryItem);
        }

        memory.setTotal(ConvertUtil.formatCapacity(globalMemory.getTotal(), true, 0));
        memory.setAvailable(ConvertUtil.formatCapacity(globalMemory.getAvailable(), true, 1));
        memory.setUse(ConvertUtil.formatCapacity(globalMemory.getTotal() - globalMemory.getAvailable(), true, 0));
        memory.setVirtualInUse(ConvertUtil.formatCapacity(globalMemory.getVirtualMemory().getVirtualInUse(), true, 0));
        memory.setVirtualMax(ConvertUtil.formatCapacity(globalMemory.getVirtualMemory().getVirtualMax(), true, 0));
        memory.setSwapTotal(ConvertUtil.formatCapacity(globalMemory.getVirtualMemory().getSwapTotal(), true, 0));
        memory.setNumber(physicalMemoryList.size());
        memory.setMemoryItems(memoryItems);
        return memory;
    }

    /**
     * 实时内存使用量
     * @return 使用容量（MB）
     */
    public static long memoryUsage(SystemInfo systemInfo) {
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        long mb = 1024 * 1024;
        long useByte = globalMemory.getTotal() - globalMemory.getAvailable();
        return useByte / mb;
    }

    /**
     * 获取磁盘信息
     */
    public static List<DiskStore> getDiskStore(SystemInfo systemInfo) {
        List<HWDiskStore> hwDiskStoreList = systemInfo.getHardware().getDiskStores();

        List<DiskStore> diskStoreList = new ArrayList<>();

        for (HWDiskStore hwDiskStore : hwDiskStoreList) {
            List<HWPartition> hsPartitionList = hwDiskStore.getPartitions();
            List<DiskPartition> diskPartitions = new ArrayList<>();
            for (HWPartition hwPartition : hsPartitionList) {
                DiskPartition diskPartition = new DiskPartition();
                diskPartition.setUuid(hwPartition.getUuid());
                diskPartition.setMajor(hwPartition.getMajor());
                diskPartition.setMinor(hwPartition.getMinor());
                diskPartition.setItemName(hwPartition.getName());
                diskPartition.setItemType(hwPartition.getType());
                diskPartition.setItemSize(ConvertUtil.formatCapacity(hwPartition.getSize(), true, 0));
                diskPartition.setMountPoint(hwPartition.getMountPoint());
                diskPartition.setIdentification(hwPartition.getIdentification());
                diskPartitions.add(diskPartition);
            }

            DiskStore diskStore = new DiskStore();
            diskStore.setSerial(hwDiskStore.getSerial());
            diskStore.setDiskName(hwDiskStore.getName());
            diskStore.setDiskModel(hwDiskStore.getModel());
            diskStore.setDiskSize(hwDiskStore.getSize());
            diskStore.setCurrentQueueLength(hwDiskStore.getCurrentQueueLength());
            diskStore.setReadBytes(hwDiskStore.getReadBytes());
            diskStore.setReads(hwDiskStore.getReads());
            diskStore.setWriteBytes(hwDiskStore.getWriteBytes());
            diskStore.setWrites(hwDiskStore.getWrites());
            diskStore.setUpdateTime(DateUtil.timestampToString(hwDiskStore.getTimeStamp(), "yyyy-MM-dd HH:mm:ss"));
            diskStore.setTransferTime(hwDiskStore.getTransferTime());
            diskStore.setNumber(hsPartitionList.size());
            diskStore.setDiskPartitions(diskPartitions);
            diskStoreList.add(diskStore);
        }
        return diskStoreList;
    }

    /**
     * 实时磁盘读写量(Byte)
     * @param lastRead 上次读数 首次填0
     * @param lastWrite 上次写数  首次填0
     * @param isFirst 是否是第一次
     * @return 磁盘读写 数组位0：读量 1：写量 2：读数 3：写数
     */
    public static long[] distReadWrite(SystemInfo systemInfo, long lastRead, long lastWrite, boolean isFirst) {
        List<HWDiskStore> hwDiskStoreList = systemInfo.getHardware().getDiskStores();
        long reads = 0L;
        long writes = 0L;
        long readBytes = 0L;
        long writeBytes = 0L;
        for (HWDiskStore hwDiskStore : hwDiskStoreList) {
            readBytes += hwDiskStore.getReadBytes();
            writeBytes += hwDiskStore.getWriteBytes();
        }
        if(!isFirst) {
            reads = readBytes - lastRead;
            writes = writeBytes - lastWrite;
        }
        long[] useByte = new long[4];
        useByte[0] = reads;
        useByte[1] = writes;
        useByte[2] = readBytes;
        useByte[3] = writeBytes;
        return useByte;
    }

    /**
     * 获取网络信息
     */
    public static List<Network> getEthernet(SystemInfo systemInfo) {
        List<NetworkIF> networkIFList = systemInfo.getHardware().getNetworkIFs();

        List<Network> networkList = new ArrayList<>();
        for (NetworkIF networkIF : networkIFList) {
            String[] ipV4 = networkIF.getIPv4addr();
            String[] ipV6 = networkIF.getIPv6addr();
            Short[] prefixLengths = networkIF.getPrefixLengths();
            Short[] subnetMasks = networkIF.getSubnetMasks();
            String ipAddress4 = "";
            String maskV4 = "";
            String ipAddress6 = "";
            Integer lenV6 = null;
            if(ipV4.length > 0) {
                ipAddress4 = ipV4[0];
            }
            if(ipV6.length > 0) {
                ipAddress6 = ipV6[0];
            }
            if(prefixLengths.length > 0) {
                lenV6 = Integer.valueOf(prefixLengths[0]);
            }
            if(subnetMasks.length > 0) {
                maskV4 = ConvertUtil.cidrToNetmask(subnetMasks[0]);
            }

            Network network = new Network();
            network.setIndex(networkIF.getIndex());
            network.setName(networkIF.getName());
            network.setDisplayName(networkIF.getDisplayName());
            network.setIfAlias(networkIF.getIfAlias());
            network.setMacAddress(networkIF.getMacaddr());
            network.setIpAddress(ipAddress4);
            network.setSubnetMask(maskV4);
            network.setIpAddress6(ipAddress6);
            network.setPrefixLenIp6(lenV6);
            network.setMtu(networkIF.getMTU() + "Byte");
            network.setSpeed(ConvertUtil.formatSpeed(networkIF.getSpeed()));
            network.setBytesRecv(networkIF.getBytesRecv());
            network.setBytesSend(networkIF.getBytesSent());
            network.setPacketsRecv(networkIF.getPacketsRecv());
            network.setPacketsSend(networkIF.getPacketsSent());
            network.setIsKnownVm(networkIF.isKnownVmMacAddr());
            networkList.add(network);
        }
        return networkList;
    }

    /**
     * 实时网络吞吐量(Byte)
     * @param lastRecv 上次接收数 首次填0
     * @param lastSend 上次发送数  首次填0
     * @param isFirst 是否是第一次
     * @return 磁盘读写 数组位0：接收量 1：发送量 2：接收数 3：发送数
     */
    public static long[] netThroughput(SystemInfo systemInfo, long lastRecv, long lastSend, boolean isFirst) {
        List<NetworkIF> networkIFList = systemInfo.getHardware().getNetworkIFs();
        long recvs = 0L;
        long sends = 0L;
        long recvBytes = 0L;
        long sendBytes = 0L;
        for (NetworkIF networkIF : networkIFList) {
            recvBytes += networkIF.getBytesRecv();
            sendBytes += networkIF.getBytesSent();
        }
        if(!isFirst) {
            recvs = recvBytes - lastRecv;
            sends = sendBytes - lastSend;
        }
        long[] useByte = new long[4];
        useByte[0] = recvs;
        useByte[1] = sends;
        useByte[2] = recvBytes;
        useByte[3] = sendBytes;
        return useByte;
    }

    public static void main(String[] args) {
//        long recvBytes = 0L;
//        long sendBytes = 0L;
//        for (int i = 0; i < 60; i++) {
//            long[] speeds;
//            if(i == 0) {
//                speeds = netThroughput(recvBytes, sendBytes, true);
//            } else {
//                speeds = netThroughput(recvBytes, sendBytes, false);
//            }
//            recvBytes = speeds[2];
//            sendBytes = speeds[3];
//            System.out.println("------------------------------------------");
//            System.out.println("接收:" + speeds[0]);
//            System.out.println("发送:" + speeds[1]);
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
        int second = 14;
        second = second / 10 * 10;
        System.out.println(second);
    }
}
