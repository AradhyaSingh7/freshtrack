package com.freshtrack.freshtrack.supplier;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.freshtrack.freshtrack.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    public SupplierResponse create(SupplierRequest request){
        Supplier supplier = Supplier.builder()
        .name(request.getName())
        .contactPerson(request.getContactPerson())
        .phone(request.getPhone())
        .email(request.getEmail())
        .build();
        Supplier saved = supplierRepository.save(supplier);
        return toResponse(saved);
    }
    public List<SupplierResponse> getAll(){
        return supplierRepository.findAll()
        .stream()
        .map(this:: toResponse)
        .collect(Collectors.toList());
    }

    public SupplierResponse getById(Long id){
        Supplier supplier = supplierRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Supplier not found with id: "+id));
        return toResponse(supplier);
    }
    public SupplierResponse update(Long id, SupplierRequest request){
        Supplier supplier = supplierRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Supplier not found with id: "+id));
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());

        Supplier saved = supplierRepository.save(supplier);
        return toResponse(saved);
    }
    public void delete(Long id){
        if(!supplierRepository.existsById(id)){
            throw new ResourceNotFoundException("Supplier not found with id: "+id);
        }
        supplierRepository.deleteById(id);
    }
    public List<SupplierReliabilityResponse> getReliabilityRankings() {
    return supplierRepository.getSupplierReliability()
            .stream()
            .map(row -> SupplierReliabilityResponse.builder()
                    .supplierId(((Number) row[0]).longValue())
                    .supplierName((String) row[1])
                    .totalDeliveries(((Number) row[2]).longValue())
                    .avgShelfLifeDays(new java.math.BigDecimal(
                        row[3].toString()))
                    .freshnessRank(((Number) row[4]).longValue())
                    .build())
            .collect(Collectors.toList());
}

    private SupplierResponse toResponse(Supplier supplier){
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}
