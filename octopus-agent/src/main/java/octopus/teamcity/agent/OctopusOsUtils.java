/*
 * Copyright 2000-2012 Octopus Deploy Pty. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package octopus.teamcity.agent;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OctopusOsUtils {

    private static final Logger LOGGER = Loggers.AGENT;

    public static Boolean CanRunOcto(@NotNull BuildAgentConfiguration agentConfiguration){
        if (agentConfiguration.getSystemInfo().isUnix()) {
            if(HasDotNet(agentConfiguration)){
                LOGGER.info("Octopus can run on agent with Unix and DotNot");
                return true;
            } else {
                if (HasOcto(agentConfiguration)) {
                    LOGGER.info("Octopus can run on agent with Unix and Octo.exe");
                    return true;
                } else {
                    LOGGER.info("Octopus can not run on agent with Unix and without Octo.exe or DotNET");
                    return false;
                }
            }
        } else if (agentConfiguration.getSystemInfo().isWindows()) {
            LOGGER.info("Octopus can run on agent with Windows");
            return true;
        }

        LOGGER.info("Octopus cannot run on agent without Windows or Unix");
        return false;
    }

    public static Boolean HasDotNet(@NotNull BuildAgentConfiguration agentConfiguration){
        String result = executeCommand("dotnet");
        // v1 of dotnet outputs Microsoft, v2 outputs Usage
        return result.contains("Microsoft") || result.contains("Usage");
    }

    public static Boolean HasOcto(@NotNull BuildAgentConfiguration agentConfiguration){
        String result = executeCommand("Octo");
        return result.contains("Octopus");
    }

    private static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
