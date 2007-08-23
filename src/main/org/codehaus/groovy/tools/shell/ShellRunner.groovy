/*
 * Copyright 2003-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.tools.shell

import org.codehaus.groovy.tools.shell.util.Logger

/**
 * Support for running a {@link Shell}.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
abstract class ShellRunner
    implements Runnable
{
    protected final Logger log = Logger.create(this.class)
    
    final Shell shell
    
    final IO io
    
    boolean breakOnNull = true
    
    ShellRunner(final Shell shell) {
        assert shell
        
        this.shell = shell
        
        this.io = shell.io
    }
    
    void run() {
        log.debug('Running')
        
        def running = true
        
        while (running) {
            try {
                running = work()
            }
            catch (ExitNotification n) {
                throw n
            }
            catch (Throwable t) {
                //
                // FIXME: Should not be debug here... ?
                //
                
                log.debug("Work failed: $t", t)
                
                //
                // TODO: Need to deal with this better...
                //
                
                io.err.println("ERROR: $t")
                
                // t.printStackTrace(io.err)
            }
        }
        
        log.debug('Finished')
    }
    
    protected boolean work() {
        def line = readLine()
        
        if (log.debugEnabled) {
            log.debug("Read line: $line")
        }
        
        // Stop on null (maybe)
        if (line == null && breakOnNull) {
            return false // stop the loop
        }
        
        // Ingore empty lines
        if (line.trim().size() > 0) {
            def result = shell << line
        }
        
        return true
    }
    
    protected abstract String readLine()
}

