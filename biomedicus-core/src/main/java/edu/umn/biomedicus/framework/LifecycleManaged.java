/*
 * Copyright (c) 2018 Regents of the University of Minnesota.
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

package edu.umn.biomedicus.framework;

import edu.umn.biomedicus.exc.BiomedicusException;

/**
 * Indicates a class that has some kind of resource that needs to be shut-down
 * or closed or freed at the end of the lifecycle of the application.
 *
 * @since 1.6.0
 */
public interface LifecycleManaged {

  /**
   *
   * @throws BiomedicusException
   */
  void doShutdown() throws BiomedicusException;
}
