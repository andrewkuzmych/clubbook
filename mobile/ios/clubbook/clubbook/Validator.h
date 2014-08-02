//
//  Validator.h
//  Clubbook
//
//  Created by Andrew on 6/20/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Validator : NSObject
+(BOOL) NSStringIsValidEmail:(NSString *)checkString;

+(BOOL) NSStringLength:(NSString *) string :(NSUInteger) length;
@end
