/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.server.filters;

import com.docdoku.core.common.BinaryResource;
import com.docdoku.core.document.DocumentIterationKey;
import com.docdoku.core.document.DocumentRevision;
import com.docdoku.core.document.DocumentRevisionKey;
import com.docdoku.core.exceptions.*;
import com.docdoku.core.product.PartIterationKey;
import com.docdoku.core.product.PartRevision;
import com.docdoku.core.product.PartRevisionKey;
import com.docdoku.core.security.UserGroupMapping;
import com.docdoku.core.services.IDocumentManagerLocal;
import com.docdoku.core.services.IDocumentResourceGetterManagerLocal;
import com.docdoku.core.services.IProductInstanceManagerLocal;
import com.docdoku.core.services.IProductManagerLocal;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RunAs;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.InputStream;

@DeclareRoles(UserGroupMapping.GUEST_PROXY_ROLE_ID)
@RunAs(UserGroupMapping.GUEST_PROXY_ROLE_ID)
@LocalBean
@Stateless
public class GuestProxy{

    @Inject
    private IProductManagerLocal productService;

    @Inject
    private IProductInstanceManagerLocal productInstanceManagerLocal;

    @Inject
    private IDocumentManagerLocal documentService;

    @Inject
    private IDocumentResourceGetterManagerLocal documentResourceGetterService;

    public PartRevision getPublicPartRevision(PartRevisionKey partRevisionKey) throws UserNotFoundException, WorkspaceNotFoundException, UserNotActiveException, PartRevisionNotFoundException, AccessRightException {
        PartRevision partRevision = productService.getPartRevision(partRevisionKey);
        return partRevision.isPublicShared() ? partRevision : null;
    }

    public DocumentRevision getPublicDocumentRevision(DocumentRevisionKey documentRevisionKey) throws NotAllowedException, WorkspaceNotFoundException, UserNotFoundException, DocumentRevisionNotFoundException, UserNotActiveException, AccessRightException {
        DocumentRevision documentRevision =  documentService.getDocumentRevision(documentRevisionKey);
        return documentRevision.isPublicShared() ? documentRevision : null;
    }

    public BinaryResource getPublicBinaryResourceForDocument(String fullName)
            throws AccessRightException, NotAllowedException, EntityNotFoundException, UserNotActiveException{

        BinaryResource binaryResource = documentService.getBinaryResource(fullName);
        String workspaceId = binaryResource.getWorkspaceId();
        String documentMasterId = binaryResource.getHolderId();
        String documentVersion = binaryResource.getHolderRevision();
        DocumentRevision documentRevision =  documentService.getDocumentRevision(new DocumentRevisionKey(workspaceId,documentMasterId,documentVersion));

        return documentRevision.isPublicShared() ? binaryResource : null;
    }

    public BinaryResource getPublicBinaryResourceForPart(String fullName)
            throws AccessRightException, NotAllowedException, EntityNotFoundException, UserNotActiveException{

        BinaryResource binaryResource = productService.getBinaryResource(fullName);
        String workspaceId = binaryResource.getWorkspaceId();
        String partNumber = binaryResource.getHolderId();
        String partVersion = binaryResource.getHolderRevision();
        PartRevision partRevision = productService.getPartRevision(new PartRevisionKey(workspaceId, partNumber, partVersion));

        return partRevision.isPublicShared() ? binaryResource:null;
    }


/*


    public BinaryResource getPublicBinaryResourceForDocument(DocumentRevisionKey docRK, String fullName)
            throws AccessRightException, NotAllowedException, EntityNotFoundException, UserNotActiveException, LoginException{

        DocumentRevision documentRevision =  documentService.getDocumentRevision(docRK);
        if(documentRevision.isPublicShared()){
            return documentService.getBinaryResource(fullName);
        }else{
            return null;
        }

    }

    public BinaryResource getPublicBinaryResourceForPart(PartRevisionKey partK, String fullName) throws UserNotFoundException, WorkspaceNotFoundException, UserNotActiveException, PartRevisionNotFoundException, LoginException, FileNotFoundException, NotAllowedException, AccessRightException {
        getPublicPartRevision(partK);
        return productService.getBinaryResource(fullName);
    }

    public DocumentIteration findDocumentIterationByBinaryResource(BinaryResource binaryResource) throws UserNotFoundException, UserNotActiveException, WorkspaceNotFoundException {
        return documentService.findDocumentIterationByBinaryResource(binaryResource);
    }

    public User whoAmI() {
       return new User();
    }

    public BinaryResource getBinaryResourceForSharedDocument(String fullName) throws AccessRightException, NotAllowedException, WorkspaceNotFoundException, UserNotFoundException, FileNotFoundException, UserNotActiveException {
        return documentService.getBinaryResource(fullName);
    }

    public BinaryResource getBinaryResourceForSharedPart(String fullName) throws AccessRightException, NotAllowedException, WorkspaceNotFoundException, UserNotFoundException, FileNotFoundException, UserNotActiveException {
        return productService.getBinaryResource(fullName);
    }

*/

    public BinaryResource getBinaryResourceForSharedDocument(String fullName) throws AccessRightException, NotAllowedException, WorkspaceNotFoundException, UserNotFoundException, FileNotFoundException, UserNotActiveException {
        return documentService.getBinaryResource(fullName);
    }

    public BinaryResource getBinaryResourceForSharedPart(String fullName) throws AccessRightException, NotAllowedException, WorkspaceNotFoundException, UserNotFoundException, FileNotFoundException, UserNotActiveException {
        return productService.getBinaryResource(fullName);
    }

    public boolean canAccess(DocumentIterationKey docIKey) throws EntityNotFoundException, UserNotActiveException{
        return documentService.canAccess(docIKey);
    }
    public boolean canAccess(PartIterationKey partIKey) throws EntityNotFoundException, UserNotActiveException{
        return productService.canAccess(partIKey);
    }

    public InputStream getDocumentConvertedResource(String outputFormat, BinaryResource binaryResource) throws UserNotFoundException, WorkspaceNotFoundException, UserNotActiveException, ConvertedResourceException {
        return documentResourceGetterService.getDocumentConvertedResource(outputFormat, binaryResource);
    }

    public InputStream getPartConvertedResource(String outputFormat, BinaryResource binaryResource) throws UserNotFoundException, WorkspaceNotFoundException, UserNotActiveException, ConvertedResourceException {
        return documentResourceGetterService.getPartConvertedResource(outputFormat, binaryResource);
    }

    public BinaryResource getBinaryResourceForProducInstance(String fullName) throws UserNotActiveException, WorkspaceNotFoundException, UserNotFoundException, FileNotFoundException, NotAllowedException, AccessRightException {
        return productInstanceManagerLocal.getBinaryResource(fullName);
    }

    public BinaryResource getBinaryResourceForPathData(String fullName) throws UserNotActiveException, WorkspaceNotFoundException, UserNotFoundException, FileNotFoundException, NotAllowedException, AccessRightException {
        return productInstanceManagerLocal.getPathDataBinaryResource(fullName);
    }
}
