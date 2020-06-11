/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.arns;

import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

/**
 * An additional model within {@link Arn} that provides the Resource Type, Resource, and
 * Resource Qualifier of an AWS Arn when those values are present and correctly formatted
 * within an Arn.
 *
 * <p>
 * If {@link #resourceType} is not present, {@link #resource} will return the entire resource
 * as a string the same as {@link Arn#resource()}.
 *
 * @see Arn
 */
@SdkPublicApi
public final class ArnResource implements ToCopyableBuilder<ArnResource.Builder, ArnResource> {

    private final String resourceType;
    private final String resource;
    private final String qualifier;

    private ArnResource(DefaultBuilder builder) {
        this.resourceType = builder.resourceType;
        this.resource = Validate.paramNotBlank(builder.resource, "resource");
        this.qualifier = builder.qualifier;
    }

    /**
     * @return the optional resource type
     */
    public Optional<String> resourceType() {
        return Optional.ofNullable(resourceType);
    }

    /**
     * @return the entire resource as a string
     */
    public String resource() {
        return resource;
    }

    /**
     * @return the optional resource qualifier
     */
    public Optional<String> qualifier() {
        return Optional.ofNullable(qualifier);
    }

    /**
     * @return a builder for {@link ArnResource}.
     */
    public static Builder builder() {
        return new DefaultBuilder();
    }

    /**
     * Parses a string containing either a resource, resource type and resource or
     * resource type, resource and qualifier into an {@link ArnResource}.
     * <p>
     * Matches:
     * <p><pre>
     * resource-id
     * resource-type:resource-id
     * resource-type/resource-id
     * resource-type:resource-id:qualifier
     * resource-type/resource-id:qualifier
     * </pre><p>
     * resource-id can be a resource name or a resource path.
     *
     * @param resource - The resource string to parse.
     * @return {@link ArnResource}
     */
    public static ArnResource fromString(String resource) {
        Integer resourceTypeBoundary = null;
        Integer resourceIdBoundary = null;

        for (int i = 0; i < resource.length(); ++i) {
            char ch = resource.charAt(i);

            if (ch == ':' || ch == '/') {
                resourceTypeBoundary = i;
                break;
            }
        }

        if (resourceTypeBoundary != null) {
            for (int i = resource.length() - 1; i > resourceTypeBoundary; --i) {
                char ch = resource.charAt(i);

                if (ch == ':') {
                    resourceIdBoundary = i;
                    break;
                }
            }
        }

        if (resourceTypeBoundary == null) {
            // 'resource-id'
            return ArnResource.builder().resource(resource).build();
        } else if (resourceIdBoundary == null) {
            // 'resource-type:resource-id'
            String resourceType = resource.substring(0, resourceTypeBoundary);
            String resourceId = resource.substring(resourceTypeBoundary + 1);
            return ArnResource.builder().resourceType(resourceType).resource(resourceId).build();
        } else {
            // 'resource-type:resource-id:qualifier'
            String resourceType = resource.substring(0, resourceTypeBoundary);
            String resourceId = resource.substring(resourceTypeBoundary + 1, resourceIdBoundary);
            String qualifier = resource.substring(resourceIdBoundary + 1);
            return ArnResource.builder()
                              .resourceType(resourceType)
                              .resource(resourceId)
                              .qualifier(qualifier)
                              .build();

        }
    }

    @Override
    public String toString() {
        return this.resourceType
               + ":"
               + this.resource
               + ":"
               + this.qualifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ArnResource that = (ArnResource) o;

        if (!Objects.equals(resourceType, that.resourceType)) {
            return false;
        }
        if (!Objects.equals(resource, that.resource)) {
            return false;
        }
        return Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {
        int result = resourceType != null ? resourceType.hashCode() : 0;
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }

    @Override
    public Builder toBuilder() {
        return builder()
            .resource(resource)
            .resourceType(resourceType)
            .qualifier(qualifier);
    }


    public interface Builder extends CopyableBuilder<ArnResource.Builder, ArnResource> {

        /**
         * Define the type of the resource.
         *
         * @param resourceType the partition that the resource is in
         * @return Returns a reference to this builder
         */
        Builder resourceType(String resourceType);

        /**
         * Define the entire resource.
         *
         * @param resource the entire resource
         * @return Returns a reference to this builder
         */
        Builder resource(String resource);

        /**
         * Define the qualifier of the resource.
         *
         * @param qualifier the qualifier of the resource
         * @return Returns a reference to this builder
         */
        Builder qualifier(String qualifier);

        /**
         * @return an instance of {@link ArnResource} that is created from the builder
         */
        ArnResource build();
    }

    public static final class DefaultBuilder implements Builder {
        private String resourceType;
        private String resource;
        private String qualifier;

        private DefaultBuilder() {
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        @Override
        public Builder resourceType(String resourceType) {
            setResourceType(resourceType);
            return this;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        @Override
        public Builder resource(String resource) {
            setResource(resource);
            return this;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public Builder qualifier(String qualifier) {
            setQualifier(qualifier);
            return this;
        }

        @Override
        public ArnResource build() {
            return new ArnResource(this);
        }
    }
}