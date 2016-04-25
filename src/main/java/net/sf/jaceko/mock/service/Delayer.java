/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Service.
 *
 *     SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Service is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.service;

import org.apache.log4j.Logger;

import java.text.MessageFormat;

public class Delayer {
    private static final Logger LOG = Logger.getLogger(Delayer.class);
    private static final int MILIS_IN_SEC = 1000;

    public void delaySec(int sec) {

        if (sec == 0)
            return;
        LOG.debug(MessageFormat.format("Delaying request for {0} seconds.", sec));
        try {
            Thread.sleep(sec * MILIS_IN_SEC);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

}
