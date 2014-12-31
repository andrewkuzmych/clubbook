//
//  MainMenuCollectionViewCell.m
//  Clubbook
//
//  Created by Anton Semenyuk on 12/29/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "MainMenuCollectionViewCell.h"

@implementation MainMenuCollectionViewCell
{
    NSTimer *timer;
    UIColor *standardBackgroundColor;
}

-(void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];

    self.icon.backgroundColor = [UIColor colorWithRed:0.966 green:0.994 blue:1.000 alpha:0.450];
    
    timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(setStandardBackground) userInfo:nil repeats:NO];
    
    [self setNeedsDisplay];
}

-(void)setIconBackground:(UIColor*) color {
    standardBackgroundColor = color;
    self.icon.backgroundColor = standardBackgroundColor;
}

-(void)setStandardBackground {
    self.icon.backgroundColor = standardBackgroundColor;
}

@end