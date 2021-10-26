package com.fictio.parrot.jusb;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.usb.*;
import java.util.List;

@Slf4j
public class DeviceDemo {
    private static final short VENDOR_ID = 0x2341;
    private static final short PRODUCT_ID = 0x43;
    private static UsbPipe pipe81, pipe01;

    public UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
        {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub())
            {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }

    public static UsbDevice findMissileLauncher(UsbHub hub) {
        UsbDevice launcher = null;

        for (UsbDevice device: (List<UsbDevice>) hub.getAttachedUsbDevices())
        {
            if (device.isUsbHub())
            {
                launcher = findMissileLauncher((UsbHub) device);
                if (launcher != null) return launcher;
            }
            else
            {
                UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
                if (desc.idVendor() == VENDOR_ID &&
                        desc.idProduct() == PRODUCT_ID) return device;
            }
        }
        return null;
    }

    @Test
    public void test(){
        UsbDevice device;
        try {
            device = findMissileLauncher(UsbHostManager.getUsbServices().getRootUsbHub());
            if (device == null) {
                log.error("Missile launcher not found.");
                return;
            }
        } catch (UsbException e) {
            e.printStackTrace();
        }
    }
}
