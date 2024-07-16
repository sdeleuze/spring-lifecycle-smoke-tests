/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.lifecycle.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;

import org.springframework.lifecycle.gradle.tasks.UpdateConcoursePipeline;
import org.springframework.lifecycle.gradle.tasks.UpdateStatusPage;

/**
 * Plugin for a project that aggregates the smoke tests to provide status and a CI
 * pipeline.
 *
 * @author Andy Wilkinson
 * @author Sebastien Deleuze
 */
public class LifecycleSmokeTestAggregatorPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		Configuration smokeTests = project.getConfigurations().create("smokeTests");
		project.getTasks().register("describeSmokeTests", Sync.class, (sync) -> {
			sync.into(project.getLayout().getBuildDirectory().dir("smoke-tests"));
			sync.from(smokeTests);
		});
		TaskProvider<UpdateStatusPage> updateStatusPage = project.getTasks()
			.register("updateStatusPage", UpdateStatusPage.class, (task) -> {
				task.setSmokeTests(smokeTests);
				task.getOutputFile().set(project.file("STATUS.adoc"));
			});
		TaskProvider<UpdateConcoursePipeline> updateConcoursePipeline = project.getTasks()
			.register("updateConcoursePipeline", UpdateConcoursePipeline.class, (task) -> {
				task.setSmokeTests(smokeTests);
				task.getOutputFile().set(project.file("ci/smoke-tests.yml"));
			});
		project.getTasks()
			.register("updateInfrastructure", (task) -> task.dependsOn(updateStatusPage, updateConcoursePipeline));
	}

}
