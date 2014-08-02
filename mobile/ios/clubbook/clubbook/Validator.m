//
//  Validator.m
//  Clubbook
//
//  Created by Andrew on 6/20/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "Validator.h"

@implementation Validator

+(BOOL) NSStringIsValidEmail:(NSString *)checkString
{
    BOOL stricterFilter = YES;
    NSString *stricterFilterString = @"[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}";
    NSString *laxString = @".+@([A-Za-z0-9]+\\.)+[A-Za-z]{2}[A-Za-z]*";
    NSString *emailRegex = stricterFilter ? stricterFilterString : laxString;
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    return [emailTest evaluateWithObject:checkString];
}

+(BOOL) NSStringLength:(NSString *) string :(NSUInteger) length
{
    if([string length] >= length)
    {
        return YES;
    }
    
    return NO;
}

@end
