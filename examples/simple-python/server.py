#!/usr/bin/env python

import os
import SimpleHTTPServer
import SocketServer

def main(port):
    Handler = SimpleHTTPServer.SimpleHTTPRequestHandler
    httpd = SocketServer.TCPServer(("", port), Handler)
    print "serving at port", port
    httpd.serve_forever()

if __name__ == '__main__':
    PORT_NUMBER = int(os.environ.get("PORT_NUMBER", '8080'))
    main(PORT_NUMBER)
