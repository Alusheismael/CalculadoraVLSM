package com.alejo.calculadoravlsm;
public class VLSMCalculator {
    private String ip;
    private String mask;
    private int requestedSubnets;

    public VLSMCalculator(String ip, String mask, int requestedSubnets) {
        this.ip = ip;
        this.mask = mask;
        this.requestedSubnets = requestedSubnets;
    }

    public String calculate() {
        StringBuilder result = new StringBuilder();
        int cidr = maskToCIDR(mask);
        int availableBits = 32 - cidr;
        int maxSubnets = (int) Math.pow(2, availableBits);

        if (requestedSubnets > maxSubnets) {
            return "Error: No hay suficientes bits para dividir en " + requestedSubnets + " subredes.";
        }

        int newCIDR = cidr;
        while (Math.pow(2, 32 - newCIDR) < requestedSubnets) {
            newCIDR++;
        }

        int blockSize = (int) Math.pow(2, 32 - newCIDR);
        long baseIp = ipToLong(ip);

        result.append("CIDR base: /").append(cidr).append("\n");
        result.append("CIDR por subred: /").append(newCIDR).append(" (").append(cidrToMask(newCIDR)).append(")\n");
        result.append("Tamaño por subred: ").append(blockSize).append(" direcciones\n\n");

        for (int i = 0; i < requestedSubnets; i++) {
            long subnetIp = baseIp + (i * blockSize);
            result.append("Subred ").append(i + 1).append(":\n");
            result.append("Dirección de red: ").append(longToIp(subnetIp)).append("\n");
            result.append("Primer host: ").append(longToIp(subnetIp + 1)).append("\n");
            result.append("Último host: ").append(longToIp(subnetIp + blockSize - 2)).append("\n");
            result.append("Broadcast: ").append(longToIp(subnetIp + blockSize - 1)).append("\n\n");
        }

        return result.toString();
    }

    private int maskToCIDR(String mask) {
        String[] parts = mask.split("\\.");
        int cidr = 0;
        for (String part : parts) {
            cidr += Integer.bitCount(Integer.parseInt(part));
        }
        return cidr;
    }

    private String cidrToMask(int cidr) {
        int mask = 0xffffffff << (32 - cidr);
        return ((mask >>> 24) & 0xff) + "." +
                ((mask >>> 16) & 0xff) + "." +
                ((mask >>> 8) & 0xff) + "." +
                (mask & 0xff);
    }

    private long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = result * 256 + Integer.parseInt(part);
        }
        return result;
    }

    private String longToIp(long ipLong) {
        return ((ipLong >> 24) & 0xFF) + "." +
                ((ipLong >> 16) & 0xFF) + "." +
                ((ipLong >> 8) & 0xFF) + "." +
                (ipLong & 0xFF);
    }
}
