//
//  UIView+StringTagAdditions.m
//  Clubbook
//
//  Created by Andrew on 7/21/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "UIView+StringTagAdditions.h"
#import <objc/runtime.h>

@implementation UIView (StringTagAdditions)
NSString * const kStringTagKey = @"StringTagKey";
static NSString *kBoolTagKey = @"BoolTagKey";

- (NSString *)stringTag
{
    return objc_getAssociatedObject(self, CFBridgingRetain(kStringTagKey));
    //return objc_getAssociatedObject(self, CFBridgingRetain(kStringTagKey));
}
- (void)setStringTag:(NSString *)stringTag
{
    objc_setAssociatedObject(self, CFBridgingRetain(kStringTagKey), stringTag, OBJC_ASSOCIATION_COPY);
    
    //objc_setAssociatedObject(self, CFBridgingRetain(kStringTagKey), stringTag, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

- (BOOL)boolTag
{
    NSNumber *number = objc_getAssociatedObject(self, CFBridgingRetain(kBoolTagKey));
    return [number boolValue];
    
    //return objc_getAssociatedObject(self, CFBridgingRetain(kStringTagKeyBool));
}
- (void)setBoolTag:(BOOL)boolTag
{
    NSNumber *number = [NSNumber numberWithBool: boolTag];
    objc_setAssociatedObject(self, CFBridgingRetain(kBoolTagKey), number , OBJC_ASSOCIATION_COPY_NONATOMIC);

    //objc_setAssociatedObject(self, CFBridgingRetain(kStringTagKeyBool), stringTagBool, OBJC_ASSOCIATION_COPY_NONATOMIC);
}
@end
