import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  images: {
    unoptimized: true,
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'https://makemytrip-springboot.onrender.com/api/:path*'
      }
    ];
  }
};

export default nextConfig;

